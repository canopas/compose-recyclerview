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
                    val userDataList = List(20) { index ->
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
                        items = listOf(1) + userDataList + listOf(1) + otherUsersDataList,
                        itemBuilder = { _, index ->
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
                        },
                        onItemMove = { fromPosition, toPosition, itemType ->
                            // Update list when an item is moved
                            when (itemType) {
                                ITEM_TYPE_FIRST_HEADER -> {
                                    // Do nothing
                                }

                                ITEM_TYPE_FIRST_LIST_ITEM -> {
                                    val fromIndex = fromPosition - 1
                                    val toIndex = toPosition - 1
                                    Collections.swap(userDataList, fromIndex, toIndex)
                                }

                                ITEM_TYPE_SECOND_HEADER -> {
                                    // Do nothing
                                }

                                // ITEM_TYPE_SECOND_LIST_ITEM
                                else -> {
                                    val fromIndex = fromPosition - userDataList.size - 2
                                    val toIndex = toPosition - userDataList.size - 2
                                    Collections.swap(otherUsersDataList, fromIndex, toIndex)
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
                                // Required for effective drag and drop. Provide a non-null [ComposeRecyclerViewAdapter.ItemTypeBuilder]
                                // when enabling drag and drop functionality.
                                return when {
                                    position == 0 -> ITEM_TYPE_FIRST_HEADER // Header type
                                    position <= userDataList.size -> ITEM_TYPE_FIRST_LIST_ITEM // First list item type
                                    position == userDataList.size + 1 -> ITEM_TYPE_SECOND_HEADER // Header type
                                    else -> ITEM_TYPE_SECOND_LIST_ITEM // Second list item type
                                }
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

                        // To change layout to grid layout, uncomment the following lines
                        val gridLayoutManager = GridLayoutManager(this, 2).apply {
                            spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                                override fun getSpanSize(position: Int): Int {
                                    return if (position == 0 || position == userDataList.size + 1) {
                                        2 // To show header title at the center of the screen and span across the entire screen
                                    } else {
                                        1
                                    }
                                }
                            }
                        }
                        recyclerView.layoutManager = gridLayoutManager
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
