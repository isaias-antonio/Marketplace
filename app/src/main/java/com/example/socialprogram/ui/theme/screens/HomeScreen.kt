package com.example.socialprogram.ui.theme.screens


import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.rememberImagePainter
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.clickable
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController


@Composable
fun HomeScreen(navController: NavController) {
    var itemName by remember { mutableStateOf("") }
    var itemDescription by remember { mutableStateOf("") }
    var itemPrice by remember { mutableStateOf("") }
    var itemImageUri by remember { mutableStateOf<Uri?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var showAddItemFields by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<Item?>(null) }
    var isEditing by remember { mutableStateOf(false) }

    val itemsForSale = remember {
        mutableStateListOf(
            Item("Notebook Dell", "Notebook com 16GB RAM e 512GB SSD", "4500", ""),
            Item("Smartphone Samsung", "Galaxy S22, 128GB", "3500", "")
        )
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        itemImageUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Marketplace",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = MaterialTheme.colorScheme.primary
        )

        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Pesquisar...") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                showAddItemFields = true
                isEditing = false
                itemName = ""
                itemDescription = ""
                itemPrice = ""
                itemImageUri = null
            },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Adicionar Item")
        }

        if (showAddItemFields) {
            TextField(
                value = itemName,
                onValueChange = { itemName = it },
                label = { Text("Nome do Item") },
                modifier = Modifier.fillMaxWidth()
            )

            TextField(
                value = itemDescription,
                onValueChange = { itemDescription = it },
                label = { Text("Descrição") },
                modifier = Modifier.fillMaxWidth()
            )

            TextField(
                value = itemPrice,
                onValueChange = { itemPrice = it },
                label = { Text("Preço") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                )
            )

            Button(
                onClick = { imagePickerLauncher.launch("image/*") },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Selecionar Imagem")
            }

            itemImageUri?.let {
                Image(
                    painter = rememberImagePainter(data = it),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
            }

            Button(
                onClick = {
                    if (itemName.isNotBlank() && itemDescription.isNotBlank() && itemPrice.isNotBlank()) {
                        if (isEditing && selectedItem != null) {
                            val index = itemsForSale.indexOf(selectedItem)
                            if (index != -1) {
                                itemsForSale[index] = Item(
                                    name = itemName,
                                    description = itemDescription,
                                    price = itemPrice,
                                    imageUri = itemImageUri?.toString() ?: ""
                                )
                            }
                        } else {
                            itemsForSale.add(
                                Item(
                                    name = itemName,
                                    description = itemDescription,
                                    price = itemPrice,
                                    imageUri = itemImageUri?.toString() ?: ""
                                )
                            )
                        }
                        itemName = ""
                        itemDescription = ""
                        itemPrice = ""
                        itemImageUri = null
                        showAddItemFields = false
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(if (isEditing) "Salvar Alterações" else "Salvar Item")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            val filteredItems = itemsForSale.filter {
                it.name.contains(searchQuery, ignoreCase = true) ||
                        it.description.contains(searchQuery, ignoreCase = true)
            }

            items(filteredItems) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedItem = item },
                    colors = CardDefaults.cardColors(containerColor = Color.LightGray)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        if (item.imageUri.isNotBlank()) {
                            Image(
                                painter = rememberImagePainter(data = item.imageUri),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Text(
                            text = item.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        Text(
                            text = item.description,
                            textAlign = TextAlign.Justify,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Text(
                            text = "Kz ${item.price}",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }

        selectedItem?.let { item ->
            AlertDialog(
                onDismissRequest = { selectedItem = null },
                confirmButton = {
                    Button(onClick = {
                        isEditing = true
                        showAddItemFields = true
                        itemName = item.name
                        itemDescription = item.description
                        itemPrice = item.price
                        itemImageUri = if (item.imageUri.isNotBlank()) Uri.parse(item.imageUri) else null
                        selectedItem = null
                    }) {
                        Text("Editar")
                    }
                    Button(onClick = {
                        itemsForSale.remove(item)
                        selectedItem = null
                    }) {
                        Text("Excluir")
                    }
                    Button(onClick = { selectedItem = null }) {
                        Text("Fechar")
                    }
                },
                title = { Text(text = item.name) },
                text = {
                    Column {
                        if (item.imageUri.isNotBlank()) {
                            Image(
                                painter = rememberImagePainter(data = item.imageUri),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Text(text = "Descrição: ${item.description}")
                        Text(text = "Preço: Kz ${item.price}")
                    }
                }
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun HomeScreenPreview() {
    HomeScreen(rememberNavController())
}

data class Item(
    val name: String,
    val description: String,
    val price: String,
    val imageUri: String
)


