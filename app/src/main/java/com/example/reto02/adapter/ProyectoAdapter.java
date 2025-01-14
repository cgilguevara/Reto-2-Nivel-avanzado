package com.example.reto02.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import com.example.reto02.AddEditActivity;
import com.example.reto02.R;
import com.example.reto02.model.Proyecto;

public class ProyectoAdapter extends ArrayAdapter<Proyecto>
{ // Recursos de diseño y contexto
  private int resourceLayout;
  private Context mContext;
  // Bandera para controlar la visibilidad de los botones
  private boolean hideButtons = false;
  // Constructor del adaptador
  public ProyectoAdapter(Context context, int resource, List<Proyecto> items)
  { super(context, resource, items);
    this.resourceLayout = resource;
    this.mContext = context;
  }
  // Método para establecer si los botones deben ocultarse o mostrarse
  public void setHideButtons(boolean hide) {
    hideButtons = hide;
  }
  // Método para obtener la vista del adaptador para un elemento específico
  @Override
  public View getView(int position, View convertView, ViewGroup parent)
  { View view = convertView;
    // Si la vista es nula, inflar el diseño
    if (view == null)
    { LayoutInflater inflater = LayoutInflater.from(mContext);
      view = inflater.inflate(resourceLayout, parent, false);
    }
    // Obtener el proyecto actual
    Proyecto proyecto = getItem(position);
    // Si el producto es válido, establecer los valores en la vista
    if (proyecto != null)
    { // Obtener las vistas de nombre y precio
      TextView textViewName = view.findViewById(R.id.textViewName);
      TextView textViewDescripcion = view.findViewById(R.id.textViewDescripcion);
      TextView textViewFechaIni = view.findViewById(R.id.textViewFechaIni);
      TextView textViewFechaFin = view.findViewById(R.id.textViewFechaFin);
      // Obtener los botones de edición y eliminación
      Button buttonEdit = view.findViewById(R.id.buttonEdit);
      Button buttonDelete = view.findViewById(R.id.buttonDelete);
      // Establecer el nombre y el precio del producto en los TextView
      textViewName.setText(proyecto.getName());
      textViewDescripcion.setText(proyecto.getDescripcion());
      textViewFechaIni.setText(proyecto.getFechaIni());
      textViewFechaFin.setText(proyecto.getFechaFin());
      // Controlar la visibilidad de los botones según la variable hideButtons
      if (hideButtons)
      { buttonEdit.setVisibility(View.GONE);
        buttonDelete.setVisibility(View.GONE);
      }
      else
      { buttonEdit.setVisibility(View.VISIBLE);
        buttonDelete.setVisibility(View.VISIBLE);
      }
      // Asignar listeners a los botones
      buttonEdit.setOnClickListener(v ->
      { // Llamar al método editProduct de MainActivity con el producto a editar
        if (mContext instanceof AddEditActivity)
        { ((AddEditActivity) mContext).editProduct(proyecto);
        }
      });
      buttonDelete.setOnClickListener(v ->
      { // Llamar al método deleteProduct de MainActivity con el producto a eliminar
        if (mContext instanceof AddEditActivity)
        { ((AddEditActivity) mContext).deleteProduct(proyecto);
        }
      });
    }
    return view;
  }
}