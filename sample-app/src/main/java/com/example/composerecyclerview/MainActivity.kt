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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import com.example.compose_recyclerview.ComposeRecyclerView
import com.example.compose_recyclerview.adapter.ComposeRecyclerViewAdapter
import com.example.composerecyclerview.model.UserData
import com.example.composerecyclerview.ui.theme.ComposeRecyclerViewTheme
import java.util.Collections

const val ITEM_TYPE_FIRST_HEADER = 0
const val ITEM_TYPE_FIRST_LIST_ITEM = 1
const val ITEM_TYPE_SECOND_HEADER = 2
const val ITEM_TYPE_SECOND_LIST_ITEM = 3

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeRecyclerViewTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val userDataList = remember {
                        List(20) { index ->
                            UserData(
                                "User ${index + 1}",
                                20 + index,
                                if (index % 2 == 0) "Male" else "Female"
                            )
                        }.toMutableStateList()
                    }

                    Column {
                        Button(
                            onClick = {
                                val indexToDelete = 0
                                Log.i("LOL", "index to delete: $indexToDelete")
                                userDataList.removeAt(
                                    indexToDelete
                                )
                            }
                        ) {
                            Text("delete 0")
                        }
                        ComposeRecyclerView(
                            modifier = Modifier.fillMaxSize(),
                            itemCount = userDataList.size,
                            itemBuilder = { index ->
                                Log.d("XXX", "Size: ${userDataList.size}\tIndex:$index")
                                CustomUserItem(user = userDataList[index])
                            },
                            onItemMove = { fromPosition, toPosition, itemType ->
                                // Update list when an item is moved
                                when (itemType) {
                                    ITEM_TYPE_FIRST_LIST_ITEM -> {
                                        val fromIndex = fromPosition - 1
                                        val toIndex = toPosition - 1
                                        Collections.swap(userDataList, fromIndex, toIndex)
                                    }
                                }
                            },
                            onDragCompleted = {
                                // Update list or do API call when an item drag operation is completed
                                Log.d("MainActivity", "onDragCompleted: $it")
                            },
                            itemTypeBuilder = object : ComposeRecyclerViewAdapter.ItemTypeBuilder {
                                override fun getItemType(position: Int): Int {
                                    // Determine the item type based on the position
                                    // You can customize this logic based on your requirements
                                    return ITEM_TYPE_FIRST_LIST_ITEM
                                }
                            },
                            onScrollEnd = {
                                // Do API call when the user reaches the end of the list during scrolling
                                Log.d("MainActivity", "onScrollEnd")
                            },
                            itemTouchHelperConfig = {
                                nonDraggableItemTypes =
                                    setOf(ITEM_TYPE_FIRST_HEADER, ITEM_TYPE_SECOND_HEADER)

                                /*onMove = { recyclerView, viewHolder, target ->
                                    // Handle item move
                                }
                                onSwiped = { viewHolder, direction ->
                                    // Handle item swipe
                                }
                                // Add more customization options as needed*/
                            },
                        ) { recyclerView ->
                            recyclerView.addItemDecoration(
                                DividerItemDecoration(
                                    recyclerView.context,
                                    DividerItemDecoration.VERTICAL
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomUserItem(user: UserData) {
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
