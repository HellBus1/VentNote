package com.digiventure.ventnote.feature.backup.components

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.digiventure.ventnote.R
import com.digiventure.ventnote.feature.backup.viewmodel.AuthBaseVM

@Composable
fun SignInButton(authViewModel: AuthBaseVM) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            authViewModel.checkAuthState()
        } else {
            Toast.makeText(context, "Auth Failed", Toast.LENGTH_LONG).show()
        }
    }

    Button(
        onClick = { launcher.launch(authViewModel.getSignInIntent()) },
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.ic_google),
                contentDescription = "",
                modifier = Modifier
                    .width(24.dp)
                    .height(24.dp)
            )
            Text(
                text = stringResource(id = R.string.sign_in_with_google),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 10.dp)
            )
        }
    }
}