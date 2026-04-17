package nad.master.pa.ui.auth

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import nad.master.pa.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    val auth = remember { FirebaseAuth.getInstance() }
    val scope = rememberCoroutineScope()

    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMsg  by remember { mutableStateOf<String?>(null) }

    // Fade-in animation on first composition
    var visible by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue    = if (visible) 1f else 0f,
        animationSpec  = tween(800),
        label          = "fade"
    )
    LaunchedEffect(Unit) { visible = true }

    // Check if already signed in
    LaunchedEffect(Unit) {
        if (auth.currentUser != null) onLoginSuccess()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DarkBrown, MediumBrown, DarkBrown)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
                .alpha(alpha),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Logo / Title
            Text(
                text  = "NAD MASTER",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight    = FontWeight.Bold,
                    color         = LightCream,
                    letterSpacing = 6.sp
                )
            )
            Text(
                text  = "Personal Assistant",
                style = MaterialTheme.typography.titleMedium,
                color = WarmCream.copy(alpha = 0.7f)
            )
            Text(
                text  = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                style = MaterialTheme.typography.headlineSmall,
                color = WarmCream,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Email field
            OutlinedTextField(
                value         = email,
                onValueChange = { email = it },
                label         = { Text("Email", color = WarmCream) },
                leadingIcon   = { Icon(Icons.Filled.Email, null, tint = WarmCream) },
                singleLine    = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier      = Modifier.fillMaxWidth(),
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = WarmCream,
                    unfocusedBorderColor = WarmCream.copy(alpha = 0.4f),
                    focusedTextColor     = LightCream,
                    unfocusedTextColor   = LightCream,
                    cursorColor          = WarmCream
                ),
                shape = RoundedCornerShape(12.dp)
            )

            // Password field
            OutlinedTextField(
                value         = password,
                onValueChange = { password = it },
                label         = { Text("Password", color = WarmCream) },
                leadingIcon   = { Icon(Icons.Filled.Lock, null, tint = WarmCream) },
                trailingIcon  = {
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(
                            imageVector = if (showPassword) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = null,
                            tint = WarmCream
                        )
                    }
                },
                singleLine    = true,
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier      = Modifier.fillMaxWidth(),
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = WarmCream,
                    unfocusedBorderColor = WarmCream.copy(alpha = 0.4f),
                    focusedTextColor     = LightCream,
                    unfocusedTextColor   = LightCream,
                    cursorColor          = WarmCream
                ),
                shape = RoundedCornerShape(12.dp)
            )

            // Error message
            errorMsg?.let { msg ->
                Text(
                    text  = msg,
                    color = CriticalRed,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Sign In button
            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        errorMsg = "Please enter email and password."
                        return@Button
                    }
                    isLoading = true
                    errorMsg  = null
                    auth.signInWithEmailAndPassword(email.trim(), password)
                        .addOnSuccessListener { isLoading = false; onLoginSuccess() }
                        .addOnFailureListener { e ->
                            isLoading = false
                            errorMsg  = e.message ?: "Sign in failed."
                        }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = !isLoading,
                colors  = ButtonDefaults.buttonColors(
                    containerColor = WarmCream,
                    contentColor   = DarkBrown
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier  = Modifier.size(22.dp),
                        color     = DarkBrown,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text  = "Sign In",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
            }
        }
    }
}

