package com.g80bits.smsgateway

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
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.g80bits.smsgateway.ui.theme.SmsGatewayTheme


class MainViewModel : ViewModel() {
    var urlText by mutableStateOf("")
        private set

    fun onUrlTextChange(newText: String) {
        urlText = newText
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
    }
}

@Composable
fun Content(
    modifier: Modifier = Modifier,
    padding: PaddingValues = PaddingValues(),
    viewModel: MainViewModel
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(padding)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        content = {
            WebSocketUrlInput(viewModel = viewModel)
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    println("Conectando")
                },
                content = {
                    Text(text = "Conectar WebSocket")
                }
            )
            Button(
                onClick = {
                    println("Desconectando")
                },
                content = {
                    Text(text = "Desconectar WebSocket")
                }
            )
        }
    )
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