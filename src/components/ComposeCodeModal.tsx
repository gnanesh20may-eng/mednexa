import React, { useState } from 'react';
import { X, Code2, Copy, Check, FileCode, Smartphone } from 'lucide-react';

interface ComposeCodeModalProps {
  isOpen: boolean;
  onClose: () => void;
}

export const ComposeCodeModal: React.FC<ComposeCodeModalProps> = ({ isOpen, onClose }) => {
  if (!isOpen) return null;

  const [activeFile, setActiveFile] = useState<'MainActivity' | 'HomeScreen' | 'InsightsScreen' | 'RescueMapScreen' | 'Theme'>('MainActivity');
  const [copied, setCopied] = useState(false);

  const codeSnippets: Record<string, string> = {
    MainActivity: `package com.mediflow.ai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.mediflow.ai.ui.theme.MediFlowTheme
import com.mediflow.ai.ui.screens.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MediFlowTheme {
                MediFlowApp()
            }
        }
    }
}

@Composable
fun MediFlowApp() {
    val navController = rememberNavController()
    var selectedScreen by remember { mutableStateOf(Screen.Home) }

    Scaffold(
        topBar = {
            MediFlowTopAppBar(
                phcName = "Kallakurichi Taluk PHC",
                sector = "Rural Block 4 · TN",
                onSyncClick = { /* Trigger background sync */ }
            )
        },
        bottomBar = {
            MediFlowBottomNavigation(
                currentScreen = selectedScreen,
                onScreenSelected = { screen ->
                    selectedScreen = screen
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { HomeScreen(navController) }
            composable(Screen.Insights.route) { AIInsightsScreen(navController) }
            composable(Screen.RescueMap.route) { RescueMapScreen(navController) }
            composable(Screen.Inventory.route) { InventoryScreen(navController) }
            composable(Screen.More.route) { MoreScreen(navController) }
        }
    }
}`,

    HomeScreen: `package com.mediflow.ai.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun HomeScreen(navController: NavController) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Quick Metrics Dashboard Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Total Medicines",
                    count = "142",
                    subtitle = "Active items",
                    accentColor = Color(0xFF006A60),
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Low-Stock Alerts",
                    count = "4",
                    subtitle = "Require buffer",
                    accentColor = Color(0xFFBA1A1A),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Emergency Stock Rescue Banner
        item {
            ElevatedCard(
                colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFFFFDAD6)),
                shape = MaterialTheme.shapes.large
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Critical Emergency Alert",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF410002)
                    )
                    Text(
                        text = "Anti-Snake Venom (ASV) down to 3 vials. 2 days of buffer left during paddy harvest season.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF410002)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { navController.navigate("rescue_map") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBA1A1A))
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Find Rescue Stock Nearby")
                    }
                }
            }
        }
    }
}`,

    InsightsScreen: `package com.mediflow.ai.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun AIInsightsScreen(navController: NavController) {
    // Illustrative seasonal factor slider
    var seasonalSpikeLevel by remember { mutableStateOf(1.35f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Demo Label Banner
        AssistChip(
            onClick = {},
            label = { Text("Demonstration Predictive Model · Illustrative Velocity") }
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "AI Stockout Prevention Engine",
            style = MaterialTheme.typography.headlineSmall
        )
        Text(
            text = "Forecasts based on rural disease seasons, monsoon surges, and district delivery lags.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Risk Card List
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Renders predicted risk cards with explainability factors
        }
    }
}`,

    RescueMapScreen: `package com.mediflow.ai.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun RescueMapScreen(navController: NavController) {
    // Jetpack Compose Canvas / Google Maps Compose interface
    // Displays connected rural Primary Health Centres (PHCs)
    // and live cold-chain logistics transfers
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Inter-PHC Medicine Rescue Mesh",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Kallakurichi Taluk Network · 6 Connected PHCs",
                style = MaterialTheme.typography.bodyMedium
            )
            
            // Map Canvas showing nodes & active transfer routes
        }
    }
}`,

    Theme: `package com.mediflow.ai.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Material 3 Color Scheme for MediFlow (Teal & Emerald palette)
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF006A60),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF73F8E6),
    onPrimaryContainer = Color(0xFF00201C),
    secondary = Color(0xFF2E7D32),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFA5D6A7),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF191C1C),
    surfaceVariant = Color(0xFFDAE5E1),
    error = Color(0xFFBA1A1A),
    background = Color(0xFFFAFDFB)
)

@Composable
fun MediFlowTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography(),
        shapes = Shapes(),
        content = content
    )
}`
  };

  const handleCopy = () => {
    navigator.clipboard.writeText(codeSnippets[activeFile]);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  return (
    <div className="fixed inset-0 bg-slate-900/60 backdrop-blur-xs flex items-center justify-center p-4 z-50 animate-fade-in">
      <div className="bg-slate-900 text-slate-100 w-full max-w-2xl rounded-2xl shadow-2xl border border-slate-700 overflow-hidden flex flex-col max-h-[88vh]">
        {/* Header */}
        <div className="bg-slate-800 px-5 py-3.5 flex items-center justify-between border-b border-slate-700">
          <div className="flex items-center gap-2">
            <Smartphone className="w-5 h-5 text-teal-400" />
            <div>
              <h2 className="text-sm font-bold text-white flex items-center gap-2">
                <span>Android Jetpack Compose Code</span>
                <span className="px-2 py-0.5 rounded-full bg-teal-900/80 text-teal-300 text-[10px] font-mono border border-teal-700/50">
                  Kotlin
                </span>
              </h2>
              <p className="text-[11px] text-slate-400">
                Native architecture blueprints for MediFlow Android APK
              </p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="p-1 rounded-lg text-slate-400 hover:text-white hover:bg-slate-700 transition-colors"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Tab Selector */}
        <div className="flex items-center gap-1 px-4 py-2 bg-slate-800/60 border-b border-slate-700 overflow-x-auto text-xs">
          {(['MainActivity', 'HomeScreen', 'InsightsScreen', 'RescueMapScreen', 'Theme'] as const).map((file) => (
            <button
              key={file}
              onClick={() => setActiveFile(file)}
              className={`px-3 py-1.5 rounded-lg font-mono transition-colors flex items-center gap-1.5 ${
                activeFile === file
                  ? 'bg-teal-500/20 text-teal-300 border border-teal-500/30'
                  : 'text-slate-400 hover:text-slate-200 hover:bg-slate-700/50'
              }`}
            >
              <FileCode className="w-3.5 h-3.5" />
              <span>{file}.kt</span>
            </button>
          ))}
          <div className="ml-auto">
            <button
              onClick={handleCopy}
              className="flex items-center gap-1 px-2.5 py-1 rounded-lg text-xs bg-slate-700 hover:bg-slate-600 text-slate-200 transition-colors"
            >
              {copied ? <Check className="w-3.5 h-3.5 text-emerald-400" /> : <Copy className="w-3.5 h-3.5" />}
              <span>{copied ? 'Copied' : 'Copy'}</span>
            </button>
          </div>
        </div>

        {/* Code Content */}
        <div className="p-4 overflow-auto flex-1 font-mono text-xs text-slate-200 bg-slate-950/70 leading-relaxed select-text">
          <pre>{codeSnippets[activeFile]}</pre>
        </div>

        {/* Footer */}
        <div className="px-5 py-2.5 bg-slate-800/80 border-t border-slate-700 flex items-center justify-between text-xs text-slate-400">
          <span>Target: Android 14+ · Material 3 · Jetpack Compose BOM 2024.09.00</span>
          <button
            onClick={onClose}
            className="px-3 py-1 bg-slate-700 hover:bg-slate-600 text-white rounded-lg transition-colors"
          >
            Close
          </button>
        </div>
      </div>
    </div>
  );
};
