package com.example.reto02;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.reto02.model.Proyecto;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.example.reto02.adapter.ListaAdapter;
import com.example.reto02.helper.ProyectosDatabaseHelper;
import com.example.reto02.helper.ProyectosFirebaseHelper;
import com.example.reto02.monitor.NetworkMonitor;

public class ListaActivity extends AppCompatActivity
{ // Componentes de la interfaz de usuario
  private EditText editTextId;
  private Button buttonAdd, buttonBack;
  private ListView listViewProducts;
  // Objetos de ayuda
  private ProyectosDatabaseHelper databaseHelper;
  private ListaAdapter listaAdapter;
  @Override
  protected void onCreate(Bundle savedInstanceState)
  { super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_lista);
    initializeUIComponents();
    initializeHelpers();
    setUpEventListeners();
//CGIL    authenticateFirebaseUser();
  }
  // Inicializa los componentes de la interfaz de usuario
  private void initializeUIComponents()
  { editTextId = findViewById(R.id.editTextId);
    buttonAdd = findViewById(R.id.buttonAdd);
    buttonBack = findViewById(R.id.buttonBack);
    listViewProducts = findViewById(R.id.listViewProducts);
  // Inicializar la base de datos y el adaptador
    databaseHelper = new ProyectosDatabaseHelper(this);
    List<Proyecto> proyectos = databaseHelper.getAllProducts();
    listaAdapter = new ListaAdapter(this, R.layout.list_proyectos, proyectos);
    listViewProducts.setAdapter(listaAdapter);
  }
  // Inicializa los objetos de ayuda
  private void initializeHelpers()
  { //      networkMonitor = new NetworkMonitor(this);
    //CGIL  firebaseHelper = new ProyectosFirebaseHelper();
  }
  // Configura los eventos de los botones
  private void setUpEventListeners()
  { buttonAdd.setOnClickListener(v -> goGestion());
    buttonBack.setOnClickListener(v -> goBack());
  }
  private void goGestion()
  { Intent intent = new Intent(ListaActivity.this, AddEditActivity.class);
    startActivity(intent);
    finish(); // Cierra esta actividad para que el usuario no pueda volver atrás
  }
  private void goBack()
  { Intent intent = new Intent(ListaActivity.this, LoginActivity.class);
    startActivity(intent);
    finish(); // Cierra esta actividad para que el usuario no pueda volver atrás
  }
  // Métodos relacionados con la carga de datos
  private void loadProductsFromDatabase()
  { List<Proyecto> proyectos = databaseHelper.getAllProducts();
    updateProductList(proyectos, false);
  }
  private void updateProductList(List<Proyecto> proyectos, boolean hideButtons)
  { listaAdapter.clear();
    listaAdapter.addAll(proyectos);
    listaAdapter.setHideButtons(hideButtons);
    listaAdapter.notifyDataSetChanged();
  }
}