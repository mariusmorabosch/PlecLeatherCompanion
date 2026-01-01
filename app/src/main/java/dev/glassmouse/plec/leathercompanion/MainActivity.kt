package dev.glassmouse.plec.leathercompanion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.glassmouse.plec.leathercompanion.ui.theme.LeatherCompanionTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val state by viewModel.state.collectAsState()
            var spacingExpanded by remember { mutableStateOf(false) }
            val spacingItems = remember { IronSpacing.entries }

            LeatherCompanionTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(horizontal = 20.dp, vertical = 40.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(
                            16.dp,
                        )
                    ) {
                        Text(text = "Thread Length Calculator", fontWeight = FontWeight.Bold)

                        TextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = state.holeCount,
                            label = {
                                Text("Hole Count")
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            onValueChange = { text -> viewModel.onHoleCountChanged(text) },
                        )
                        Box {
                            TextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = state.spacing.label,
                                readOnly = true,
                                label = {
                                    Text("Spacing (mm)")
                                },
                                interactionSource = remember { MutableInteractionSource() }
                                    .also { interactionSource ->
                                        LaunchedEffect(interactionSource) {
                                            interactionSource.interactions.collect {
                                                if (it is PressInteraction.Release) {
                                                    spacingExpanded = true
                                                }
                                            }
                                        }
                                    },
                                onValueChange = { },
                            )
                            DropdownMenu(
                                expanded = spacingExpanded,
                                onDismissRequest = { spacingExpanded = false },
                                modifier = Modifier
                                    .width(IntrinsicSize.Max)
                                    .background(Color.LightGray)
                            ) {
                                spacingItems.forEach { item ->
                                    DropdownMenuItem(
                                        onClick = {
                                            viewModel.onSpacingChanged(item)
                                            spacingExpanded = false
                                        },
                                        text = { Text(text = item.label) })
                                }
                            }
                        }

                        TextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = state.thickness,
                            label = { Text("Thickness (mm)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            onValueChange = { text -> viewModel.onThicknessChanged(text) },
                        )

                        TextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = state.finishingLength,
                            label = { Text("Finishing Length (mm)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            onValueChange = { text -> viewModel.onFinishingLengthChanged(text) },
                        )

                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = viewModel::onCalculateClick,
                            enabled = state.isCalculateEnabled
                        ) {
                            Text(text = "Calculate")
                        }
                        state.totalLength?.let {
                            Text(text = "Total Length: ${state.totalLength.toString()} mm")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LeatherCompanionTheme {
        Greeting("Android")
    }
}