package com.example.composerecyclerview

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.compose_recyclerview.ComposeRecyclerView
import com.example.composerecyclerview.model.UserData
import com.example.composerecyclerview.ui.theme.ComposeRecyclerViewTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeRecyclerViewTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val userDataList = List(200) { index ->
                        UserData(
                            "User ${index + 1}",
                            20 + index,
                            if (index % 2 == 0) "Male" else "Female"
                        )
                    }

                    val otherUsersDataList = List(20) { index ->
                        UserData(
                            "User ${index + 21}",
                            20 + index,
                            if (index % 2 == 0) "Male" else "Female"
                        )
                    }

                    ComposeRecyclerView(
                        modifier = Modifier.fillMaxSize(),
                        itemCount = 1 + userDataList.size + 1 + otherUsersDataList.size,
                        itemBuilder = { index ->
                            if (index == 0) {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "First List Header Composable",
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                                return@ComposeRecyclerView
                            }

                            val userIndex = index - 1
                            if (userIndex < userDataList.size) {
                                CustomUserItem(user = userDataList[userIndex])
                                return@ComposeRecyclerView
                            }

                            if (userIndex == userDataList.size) {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "Second List Header Composable",
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                                return@ComposeRecyclerView
                            }

                            val otherUserIndex = index - userDataList.size - 2
                            if (otherUserIndex < otherUsersDataList.size) {
                                CustomUserItem(user = otherUsersDataList[otherUserIndex])
                                return@ComposeRecyclerView
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CustomUserItem(user: UserData) {

    SideEffect {
        Log.d("XXX", "SideEffect - ${user.name}")
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black,
            contentColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = "Name: ${user.name}",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Age: ${user.age}",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Gender: ${user.gender}",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
        }
    }
}
