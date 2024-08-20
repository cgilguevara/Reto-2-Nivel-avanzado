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

import com.example.reto02.adapter.ProyectoAdapter;
import com.example.reto02.helper.ProyectosDatabaseHelper;
import com.example.reto02.helper.ProyectosFirebaseHelper;
import com.example.reto02.monitor.NetworkMonitor;

public class AddEditActivity extends AppCompatActivity
{ // Componentes de la interfaz de usuario
  private EditText editTextId, editTextName, editTextDescripcion, editTextFechaIni, editTextFechaFin;
  private Button buttonAdd, buttonBack, buttonGetFirebase, buttonSichronized, buttonGetSqlite;
  private ListView listViewProducts;
  // Objetos de ayuda
  private ProyectosDatabaseHelper databaseHelper;
  private ProyectoAdapter proyectoAdapter;
  private ProyectosFirebaseHelper firebaseHelper;
  private NetworkMonitor networkMonitor;
  @Override
  protected void onCreate(Bundle savedInstanceState)
  { super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_edit);
    initializeUIComponents();
    initializeHelpers();
    setUpEventListeners();
    authenticateFirebaseUser();
  }
  // Inicializa los componentes de la interfaz de usuario
  private void initializeUIComponents()
  { // Inicializa los componentes de la interfaz de usuario
    editTextId = findViewById(R.id.editTextId);
    editTextName = findViewById(R.id.editTextName);
    editTextDescripcion = findViewById(R.id.editTextDescripcion);
    editTextFechaIni = findViewById(R.id.editTextFechaIni);
    editTextFechaFin = findViewById(R.id.editTextFechaFin);
    buttonAdd = findViewById(R.id.buttonAdd);
    buttonBack = findViewById(R.id.buttonBack);
    buttonGetFirebase = findViewById(R.id.buttonGetFirebase);
    buttonSichronized = findViewById(R.id.buttonSichronized);
    buttonGetSqlite = findViewById(R.id.buttonGetSqlite);
    listViewProducts = findViewById(R.id.listViewProducts);
    // Inicializar la base de datos y el adaptador
    databaseHelper = new ProyectosDatabaseHelper(this);
    List<Proyecto> proyectos = databaseHelper.getAllProducts();
    proyectoAdapter = new ProyectoAdapter(this, R.layout.list_item_proyect, proyectos);
    listViewProducts.setAdapter(proyectoAdapter);
  }
  // Inicializa los objetos de ayuda
  private void initializeHelpers()
  { networkMonitor = new NetworkMonitor(this);
    firebaseHelper = new ProyectosFirebaseHelper();
  }
  // Configura los eventos de los botones
  private void setUpEventListeners()
  { buttonGetSqlite.setOnClickListener(v -> loadProductsFromDatabase());
    buttonSichronized.setOnClickListener(v -> synchronizeData());
    buttonGetFirebase.setOnClickListener(v -> loadProductsFromFirebase(true));
    buttonAdd.setOnClickListener(v -> handleAddOrUpdateProduct());
    buttonBack.setOnClickListener(v -> goBack());
  }
  private void goBack()
  { Intent intent = new Intent(AddEditActivity.this, ListaActivity.class);
    startActivity(intent);
    finish(); // Cierra esta actividad para que el usuario no pueda volver atrás
  }
  // Autentica al usuario de Firebase
  private void authenticateFirebaseUser()
  { FirebaseAuth auth = FirebaseAuth.getInstance();
    auth.signInAnonymously().addOnCompleteListener(this, task ->
    { if (task.isSuccessful())
      { FirebaseUser user = auth.getCurrentUser();
        // Se puedes usar el user.getUid() para identificar al usuario si es necesario
      }
      else
      { Toast.makeText(AddEditActivity.this, "Error al iniciar sesión anónimo", Toast.LENGTH_SHORT).show();
      }
    });
  }
  // Maneja la operación de agregar o actualizar producto
  private void handleAddOrUpdateProduct()
  { if (buttonAdd.getText().toString().equalsIgnoreCase("Agregar"))
    { addProduct();
    }
    else
    { saveProduct();
    }
  }
  // Métodos relacionados con la carga de datos
  private void loadProductsFromDatabase()
  { List<Proyecto> proyectos = databaseHelper.getAllProducts();
    updateProductList(proyectos, false);
  }
  private void loadProductsFromFirebase(boolean hideButtons)
  { firebaseHelper.getAllProducts(new ProyectosFirebaseHelper.GetProductsCallback()
    { @Override
      public void onProductsRetrieved(List<Proyecto> proyectos)
      { updateProductList(proyectos, hideButtons);
      }
      @Override
      public void onError()
      { Toast.makeText(AddEditActivity.this, "Error al obtener productos de Firebase", Toast.LENGTH_SHORT).show();
      }
    });
  }
  private void updateProductList(List<Proyecto> proyectos, boolean hideButtons)
  { proyectoAdapter.clear();
    proyectoAdapter.addAll(proyectos);
    proyectoAdapter.setHideButtons(hideButtons);
    proyectoAdapter.notifyDataSetChanged();
  }
  // Métodos relacionados con la sincronización de datos
  private void synchronizeData()
  { if (!networkMonitor.isNetworkAvailable())
    { Toast.makeText(AddEditActivity.this, "No hay conexión a internet", Toast.LENGTH_SHORT).show();
    return;
    }
    synchronizeAndRemoveData();
    synchronizeAndLoadData();
  }
  private void synchronizeAndLoadData()
  { List<Proyecto> productsFromSQLite = databaseHelper.getAllProducts();
    synchronizeProductsToFirebase(productsFromSQLite);
    loadProductsFromFirebase(true);
  }
  private void synchronizeAndRemoveData()
  { firebaseHelper.getAllProducts(new ProyectosFirebaseHelper.GetProductsCallback()
    { @Override
      public void onProductsRetrieved(List<Proyecto> productsFromFirebase)
      { List<Proyecto> productsFromSQLite = databaseHelper.getAllProducts();
        Set<String> sqliteProductIds = new HashSet<>();
        for (Proyecto sqliteProyecto : productsFromSQLite)
        { sqliteProductIds.add(sqliteProyecto.getId());
        }
        List<Proyecto> productsToDeleteFromFirebase = new ArrayList<>();
        for (Proyecto firebaseProyecto : productsFromFirebase)
        { if (!sqliteProductIds.contains(firebaseProyecto.getId()))
          { productsToDeleteFromFirebase.add(firebaseProyecto);
          }
        }
      deleteProductsFromFirebase(productsToDeleteFromFirebase);
      }
      @Override
      public void onError()
      { Toast.makeText(AddEditActivity.this, "Error al obtener productos de Firebase", Toast.LENGTH_SHORT).show();
      }
    });
  }
  private void synchronizeProductsToFirebase(List<Proyecto> productsFromSQLite)
  { for (Proyecto proyecto : productsFromSQLite)
    { firebaseHelper.checkIfProductExists(proyecto.getId(), new ProyectosFirebaseHelper.ProductExistsCallback()
      { @Override
        public void onProductExists(boolean exists)
        { if (exists)
          { firebaseHelper.updateProduct(proyecto);
          }
          else
          { firebaseHelper.addProduct(proyecto, new ProyectosFirebaseHelper.AddProductCallback()
            { @Override
              public void onSuccess()
              { // Producto agregado exitosamente
              }
              @Override
              public void onError(Exception e)
              { Toast.makeText(AddEditActivity.this, "Error al agregar producto a Firebase: " + e.getMessage(), Toast.LENGTH_SHORT).show();
              }
            });
          }
        }
        @Override
        public void onError()
        { Toast.makeText(AddEditActivity.this, "Error al verificar existencia del producto en Firebase", Toast.LENGTH_SHORT).show();
        }
      });
    }
  }
  private void deleteProductsFromFirebase(List<Proyecto> productsToDeleteFromFirebase)
  { for (Proyecto proyectoToDelete : productsToDeleteFromFirebase)
    { firebaseHelper.deleteProduct(proyectoToDelete.getId(), new ProyectosFirebaseHelper.DeleteProductCallback()
      { @Override
        public void onSuccess()
        { Toast.makeText(AddEditActivity.this, "Producto eliminado de Firebase", Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onError(Exception e)
        { Toast.makeText(AddEditActivity.this, "Error al eliminar producto de Firebase: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
      });
    }
    loadProductsFromFirebase(true);
  }

  // Métodos relacionados con la manipulación de productos
  private void addProduct()
  { if (areFieldsEmpty())
    { return;
    }
    String name = editTextName.getText().toString();
    String descripcion = editTextDescripcion.getText().toString();
    String fechaini = editTextFechaIni.getText().toString();
    String fechafin = editTextFechaFin.getText().toString();
    Proyecto newProyecto = new Proyecto(name, descripcion, fechaini, fechafin);
    databaseHelper.addProduct(newProyecto);
    loadProductsFromDatabase();
    clearInputFields();
    Toast.makeText(this, "Producto agregado exitosamente", Toast.LENGTH_SHORT).show();
  }
  private void saveProduct()
  { if (areFieldsEmpty())
    { return;
    }
    String id = editTextId.getText().toString();
    String name = editTextName.getText().toString();
    String descripcion = editTextDescripcion.getText().toString();
    String fechaini = editTextFechaIni.getText().toString();
    String fechafin = editTextFechaFin.getText().toString();
    Proyecto proyecto = new Proyecto(id, name, descripcion, fechaini, fechafin);
    databaseHelper.updateProduct(proyecto);
    loadProductsFromDatabase();
    buttonAdd.setText("Agregar");
    clearInputFields();
    Toast.makeText(this, "Producto actualizado exitosamente", Toast.LENGTH_SHORT).show();
  }
  // Métodos de utilidad
  private boolean areFieldsEmpty()
  { if (editTextName.getText().toString().trim().isEmpty() || editTextDescripcion.getText().toString().trim().isEmpty() || editTextFechaIni.getText().toString().trim().isEmpty() || editTextFechaFin.getText().toString().trim().isEmpty())
    { Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
      return true;
    }
    return false;
  }
  private void clearInputFields()
  { editTextId.setText("");
    editTextName.setText("");
    editTextDescripcion.setText("");
    editTextFechaIni.setText("");
    editTextFechaFin.setText("");
  }
  public void editProduct(Proyecto proyecto)
  { editTextId.setText(proyecto.getId());
    editTextName.setText(proyecto.getName());
    editTextDescripcion.setText(proyecto.getDescripcion());
    editTextFechaIni.setText(proyecto.getFechaIni());
    editTextFechaFin.setText(proyecto.getFechaFin());
    buttonAdd.setText("Guardar");
  }
  public void deleteProduct(Proyecto proyecto)
  { if (proyecto.isDeleted())
    { Toast.makeText(this, "Producto ya está eliminado", Toast.LENGTH_SHORT).show();
    }
    else
    { databaseHelper.deleteProduct(proyecto.getId());
      loadProductsFromDatabase();
      Toast.makeText(this, "Producto eliminado exitosamente", Toast.LENGTH_SHORT).show();
    }
  }
}