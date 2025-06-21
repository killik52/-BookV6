package com.example.myapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.ActivityMainBinding
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import java.text.SimpleDateFormat
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.lifecycle.lifecycleScope
import database.entities.Fatura
import database.entities.FaturaLixeira

class MainActivityRoom : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainActivityViewModel by viewModels()
    private lateinit var faturaViewModel: FaturaViewModel
    private var isGridViewVisible = false
    private val SECOND_SCREEN_REQUEST_CODE = 1
    private val STORAGE_PERMISSION_CODE = 100
    private val LIXEIRA_REQUEST_CODE = 1002
    private lateinit var faturaAdapter: FaturaResumidaAdapter
    private var isSearchActive = false
    private var mediaPlayer: MediaPlayer? = null

    private val barcodeLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents == null) {
            // showToast("Leitura cancelada")
        } else {
            val barcodeValue = result.contents
            Log.d("MainActivity", "Código de barras lido (bruto): '$barcodeValue'")
            emitBeep()
            openInvoiceByBarcode(barcodeValue)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Log.d("MainActivity", "onCreate chamado com ViewBinding")

        // Inicializa o ViewModel de Fatura
        faturaViewModel = FaturaViewModel(application)

        try {
            mediaPlayer = MediaPlayer.create(this, R.raw.beep)
            mediaPlayer?.setOnErrorListener { _, what, extra ->
                Log.e("MainActivity", "Erro no MediaPlayer: what=$what, extra=$extra")
                showToast("Erro ao inicializar o som de beep")
                true
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Erro ao inicializar MediaPlayer: ${e.message}")
            showToast("Erro ao carregar o som de beep")
        }

        binding.recyclerViewFaturas.layoutManager = LinearLayoutManager(this)
        faturaAdapter = FaturaResumidaAdapter(
            this,
            onItemClick = { fatura ->
                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        // Busca a fatura no Room
                        faturaViewModel.getFaturaById(fatura.id).observe(this@MainActivityRoom) { faturaCompleta ->
                            faturaCompleta?.let {
                                val intent = Intent(this@MainActivityRoom, SecondScreenActivity::class.java).apply {
                                    putExtra("fatura_id", it.id)
                                    putExtra("foi_enviada", it.foiEnviada)
                                }
                                startActivityForResult(intent, SECOND_SCREEN_REQUEST_CODE)
                            } ?: run {
                                showToast("Fatura não encontrada.")
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Log.e("MainActivity", "Erro ao abrir fatura: ${e.message}")
                            showToast("Erro ao abrir fatura: ${e.message}")
                        }
                    }
                }
            },
            onItemLongClick = { fatura ->
                Log.d("MainActivity", "Iniciando exclusão da fatura ID=${fatura.id}")
                lifecycleScope.launch(Dispatchers.IO) {
                    try {
                        // Busca a fatura completa
                        faturaViewModel.getFaturaById(fatura.id).observe(this@MainActivityRoom) { faturaCompleta ->
                            faturaCompleta?.let {
                                // Cria uma nova entrada na lixeira
                                val faturaLixeira = FaturaLixeira(
                                    id = 0, // Room irá gerar o ID
                                    numeroFatura = it.numeroFatura,
                                    cliente = it.cliente,
                                    artigos = it.artigos,
                                    subtotal = it.subtotal,
                                    desconto = it.desconto,
                                    descontoPercent = it.descontoPercent,
                                    taxaEntrega = it.taxaEntrega,
                                    saldoDevedor = it.saldoDevedor,
                                    data = it.data,
                                    fotosImpressora = it.fotosImpressora,
                                    notas = it.notas
                                )
                                
                                // Insere na lixeira e remove da tabela principal
                                faturaViewModel.insertFaturaLixeira(faturaLixeira)
                                faturaViewModel.deleteFatura(it)
                                
                                showToast("Fatura movida para a lixeira!")
                                viewModel.carregarFaturas() // Pede ao ViewModel para recarregar
                            } ?: run {
                                showToast("Fatura não encontrada.")
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Log.e("MainActivity", "Erro ao mover fatura para a lixeira: ${e.message}", e)
                            showToast("Erro ao mover fatura: ${e.message}")
                        }
                    }
                }
            }
        )
        binding.recyclerViewFaturas.adapter = faturaAdapter

        // Adiciona a decoração (espaçamento)
        if (binding.recyclerViewFaturas.itemDecorationCount > 0) {
            for (i in (binding.recyclerViewFaturas.itemDecorationCount - 1) downTo 0) {
                binding.recyclerViewFaturas.getItemDecorationAt(i)?.let {
                    binding.recyclerViewFaturas.removeItemDecoration(it)
                }
            }
        }
        val spaceInDp = 4f
        val spaceInPixels = (spaceInDp * resources.displayMetrics.density).toInt()
        binding.recyclerViewFaturas.addItemDecoration(VerticalSpaceItemDecoration(spaceInPixels))

        // Observa o ViewModel
        viewModel.faturas.observe(this) { faturas ->
            faturaAdapter.updateFaturas(faturas)
            Log.d("MainActivity", "Adapter atualizado com dados do ViewModel. Total: ${faturas.size}")
        }

        val menuOptionsAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.menu_options,
            R.layout.item_menu
        )
        binding.menuGridView.adapter = menuOptionsAdapter

        binding.menuGridView.setOnItemClickListener { _, _, position, _ ->
            try {
                val selectedOption = menuOptionsAdapter.getItem(position).toString()
                when (selectedOption) {
                    "Fatura" -> toggleGridView()
                    "Cliente" -> {
                        startActivity(Intent(this, ListarClientesActivityRoom::class.java))
                        toggleGridView()
                    }
                    "Artigo" -> {
                        startActivity(Intent(this, ListarArtigosActivityRoom::class.java))
                        toggleGridView()
                    }
                    "Lixeira" -> {
                        startActivityForResult(Intent(this, LixeiraActivity::class.java), LIXEIRA_REQUEST_CODE)
                        toggleGridView()
                    }
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Erro ao abrir atividade: ${e.message}")
                showToast("Erro ao abrir a tela: ${e.message}")
            }
        }

        binding.faturaTitleContainer.setOnClickListener {
            toggleGridView()
        }

        binding.dollarIcon.setOnClickListener {
            val options = ScanOptions().apply {
                setDesiredBarcodeFormats(ScanOptions.CODE_128)
                setPrompt("Escaneie o código de barras no PDF")
                setCameraId(0)
                setBeepEnabled(false)
                setOrientationLocked(false)
            }
            barcodeLauncher.launch(options)
        }

        binding.homeIcon.setOnClickListener {
            val intent = Intent(this, MainActivityRoom::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        binding.moreIcon.setOnClickListener {
            val intent = Intent(this, DefinicoesActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        binding.searchButton.setOnClickListener {
            Log.d("MainActivity", "Botão de busca clicado")
            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.search_dialog_title))

            val input = EditText(this)
            input.hint = getString(R.string.search_dialog_hint)
            builder.setView(input)

            builder.setPositiveButton(getString(R.string.search_dialog_positive_button)) { dialog, _ ->
                val query = input.text.toString().trim()
                Log.d("MainActivity", "Botão 'Pesquisar' clicado no diálogo, termo: '$query'")
                if (query.isEmpty()) {
                    showToast(getString(R.string.search_empty_query_message))
                    viewModel.carregarFaturas()
                    isSearchActive = false
                } else {
                    buscarFaturas(query)
                    isSearchActive = true
                }
                dialog.dismiss()
            }
            builder.setNegativeButton(getString(R.string.search_dialog_negative_button)) { dialog, _ ->
                Log.d("MainActivity", "Botão 'Cancelar' clicado no diálogo")
                dialog.cancel()
            }
            builder.show()
        }

        binding.graficosButton.setOnClickListener {
            Log.d("MainActivity", "Botão de Gráficos clicado")
            val intent = Intent(this, ResumoFinanceiroActivity::class.java)
            startActivity(intent)
        }

        binding.addButton.setOnClickListener {
            requestStorageAndCameraPermissions()
        }
    }

    private fun emitBeep() {
        try {
            mediaPlayer?.let { player ->
                if (player.isPlaying) {
                    player.stop()
                    try {
                        player.prepare()
                    } catch (e: IllegalStateException){
                        Log.e("MainActivity", "Erro ao preparar MediaPlayer após stop: ${e.message}")
                        mediaPlayer?.release()
                        mediaPlayer = MediaPlayer.create(this, R.raw.beep)
                    }
                }
                player.start()
            } ?: run {
                mediaPlayer = MediaPlayer.create(this, R.raw.beep)
                mediaPlayer?.start()
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Erro ao reproduzir som de beep: ${e.message}")
        }
    }

    private fun openInvoiceByBarcode(barcodeValue: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val cleanedBarcodeValue = barcodeValue.trim()
                val faturaIdFromBarcode = cleanedBarcodeValue.toLongOrNull() ?: cleanedBarcodeValue.replace("[^0-9]".toRegex(), "").toLongOrNull()

                if (faturaIdFromBarcode == null) {
                    withContext(Dispatchers.Main) {
                        showToast("Código de barras inválido: $cleanedBarcodeValue")
                    }
                    return@launch
                }
                abrirFaturaPorId(faturaIdFromBarcode, cleanedBarcodeValue)
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("MainActivity", "Erro ao abrir fatura por código de barras: ${e.message}")
                    showToast("Erro ao abrir fatura: ${e.message}")
                }
            }
        }
    }

    private fun abrirFaturaPorId(faturaId: Long, barcodeScaneado: String) {
        faturaViewModel.getFaturaById(faturaId).observe(this) { fatura ->
            fatura?.let {
                Log.d("MainActivity", "Fatura encontrada com ID: $faturaId. Foi enviada: ${it.foiEnviada}")
                val intent = Intent(this@MainActivityRoom, SecondScreenActivity::class.java).apply {
                    putExtra("fatura_id", faturaId)
                    putExtra("foi_enviada", it.foiEnviada)
                }
                startActivityForResult(intent, SECOND_SCREEN_REQUEST_CODE)
            } ?: run {
                showToast("Fatura não encontrada com ID: $faturaId")
            }
        }
    }

    private fun buscarFaturas(query: String) {
        // TODO: Implementar busca de faturas quando o FaturaViewModel estiver completo
        Log.d("MainActivityRoom", "Busca de faturas solicitada para: $query")
        showToast("Funcionalidade de busca será implementada")
    }

    private fun toggleGridView() {
        val animation: Animation = if (isGridViewVisible) {
            AnimationUtils.loadAnimation(this, R.anim.slide_up)
        } else {
            AnimationUtils.loadAnimation(this, R.anim.slide_down)
        }

        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                binding.menuGridView.visibility = if (isGridViewVisible) View.GONE else View.VISIBLE
            }
            override fun onAnimationRepeat(animation: Animation?) {}
        })

        binding.menuGridView.startAnimation(animation)
        isGridViewVisible = !isGridViewVisible
    }

    private fun requestStorageAndCameraPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permissions = arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )

            val permissionsToRequest = permissions.filter {
                ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
            }.toTypedArray()

            if (permissionsToRequest.isNotEmpty()) {
                ActivityCompat.requestPermissions(this, permissionsToRequest, STORAGE_PERMISSION_CODE)
            } else {
                startSecondScreen()
            }
        } else {
            startSecondScreen()
        }
    }

    private fun startSecondScreen() {
        val intent = Intent(this, SecondScreenActivity::class.java)
        startActivityForResult(intent, SECOND_SCREEN_REQUEST_CODE)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                startSecondScreen()
            } else {
                showToast("Permissões necessárias não concedidas.")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SECOND_SCREEN_REQUEST_CODE || requestCode == LIXEIRA_REQUEST_CODE) {
            viewModel.carregarFaturas()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        super.onDestroy()
    }
} 