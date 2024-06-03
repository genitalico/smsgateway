package com.g80bits.smsgateway

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.g80bits.smsgateway.ui.theme.SmsGatewayTheme
import java.util.UUID


class MainViewModel : ViewModel() {
    var urlText by mutableStateOf("")
        private set

    fun onUrlTextChange(newText: String) {
        urlText = newText.trim()
    }

    var id by mutableStateOf("")
        private set

    fun onIdChange(newText: String) {
        id = newText
    }
}

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmsGatewayTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Content(
                        modifier = Modifier.padding(innerPadding),
                        viewModel = viewModel
                    )
                }
            }
        }

        val permissionManager = Permissions(this)
        permissionManager.checkPermissions()

    }
}

@Composable
fun Content(
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(),
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(padding)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        content = {
            WebSocketUrlInput(viewModel = viewModel)
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "ID: ${viewModel.id}")
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    println("Conectando")
                    startService(true, context, viewModel)

                },
                content = {
                    Text(text = "Conectar WebSocket")
                }
            )
            Button(
                onClick = {
                    println("Desconectando")
                    startService(false, context, viewModel)
                },
                content = {
                    Text(text = "Desconectar WebSocket")
                }
            )
        }
    )
}

private fun startService(start: Boolean, context: Context, viewModel: MainViewModel) {

    if (viewModel.urlText.isEmpty()) {
        return
    }

    if (start) {
        viewModel.onIdChange(UUID.randomUUID().toString())
        val serviceIntent = Intent(context, ForegroundService::class.java)
        serviceIntent.putExtra(context.getString(R.string.extra_url), viewModel.urlText)
        serviceIntent.putExtra(context.getString(R.string.extra_id), viewModel.id)
        context.startService(serviceIntent)
    } else {
        val serviceIntent = Intent(context, ForegroundService::class.java)
        serviceIntent.putExtra(context.getString(R.string.extra_stop_socket), true)
        context.startService(serviceIntent)
        context.stopService(serviceIntent)
        viewModel.onIdChange("")
    }
}

@Composable
fun WebSocketUrlInput(viewModel: MainViewModel) {
    TextField(
        value = viewModel.urlText,
        onValueChange = {
            viewModel.onUrlTextChange(it)
        },
        label = { Text("Enter WebSocket URL") },
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SmsGatewayTheme {
        Content(
            modifier = Modifier.padding(16.dp),
            viewModel = MainViewModel()
        )
    }
}