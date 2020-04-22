package com.diegop.asteroides;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Asteroides extends Activity {
	
	private TextView tTitulo;
	private Button bJugar;
	private Button bConfigurar;
	private Button bPuntuaciones;
	private Button bAcerca;
	private Button bSalir;
	
	public MediaPlayer mp;
	
	public static AlmacenPuntuaciones almacen;
	
	    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mp = MediaPlayer.create(this, R.raw.audio);
        
    	almacen = new AlmacenPuntuacionesSQLite(this);
  
        tTitulo = (TextView) findViewById(R.id.astTit);
        tTitulo.setOnLongClickListener(new View.OnLongClickListener() {
        	public boolean onLongClick(View view) {
        		easterEgg(null);
        		return true;
        	}
        });

        bJugar = (Button) findViewById(R.id.boton1);
        bJugar.setOnClickListener(new OnClickListener() {
        	public void onClick(View view){
        		lanzarJuego(null);
        	}
        });

        bConfigurar = (Button) findViewById(R.id.boton2);
        bConfigurar.setOnClickListener(new OnClickListener() {
        	public void onClick(View view){
        		lanzarPreferencias(null);
        	}
        });
        
        bPuntuaciones = (Button) findViewById(R.id.boton3);
        bPuntuaciones.setOnClickListener(new OnClickListener() {
        	public void onClick(View view) {
        		lanzarPuntuaciones(null);
        	}
        });
        
        bAcerca = (Button) findViewById(R.id.boton4);
        bAcerca.setOnClickListener(new OnClickListener() {
        	public void onClick(View view) {
        		lanzarAcerca(null);
        	}
        });
        
        bSalir = (Button) findViewById(R.id.boton5);
        bSalir.setOnClickListener(new OnClickListener(){
        	public void onClick(View view) {
        		finish();
        	}
        });
                
    }
    
    public void easterEgg(View view) {
    	Toast.makeText(this, "Jojojo Easteregg!!!", Toast.LENGTH_SHORT).show();
    }
    
    public void lanzarJuego(View view) {
    	Intent i = new Intent(this, Juego.class);
    	startActivityForResult(i, 1234);
    }
    
    public void lanzarPreferencias(View view){
    	Intent i = new Intent(this, Preferencias.class);
    	startActivity(i);
    }
    
    public void lanzarPuntuaciones(View view){
    	Intent i = new Intent(this, Puntuaciones.class);
    	startActivity(i);
    }
    
    public void lanzarAcerca(View view){
    	Intent i = new Intent(this, Acerca.class);
    	startActivity(i);
    }
    
    @Override
    protected void onStart() {
    	super.onStart();
    	//Toast.makeText(this, "onStart", Toast.LENGTH_SHORT).show();
   		mp.start();
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	//Toast.makeText(this, "onResume", Toast.LENGTH_SHORT).show();
    	mp.start();
    }
    
    @Override
    protected void onPause() {
    	//Toast.makeText(this, "onPause", Toast.LENGTH_SHORT).show();
    	mp.pause();
       	super.onPause();
    }
    
    @Override
    protected void onStop() {
    	//Toast.makeText(this, "onStop", Toast.LENGTH_SHORT).show();
    	mp.pause();
    	super.onStop();
    }
    
    @Override
    protected void onRestart() {
    	super.onRestart();
    	//Toast.makeText(this, "onRestart", Toast.LENGTH_SHORT).show();
    	mp.start();
    }
    
    @Override
    protected void onDestroy() {
    	//Toast.makeText(this, "onDestroy", Toast.LENGTH_SHORT).show();
    	super.onDestroy();
    }
    
    @Override
    protected void onSaveInstanceState(Bundle estadoGuardado) {
    	super.onSaveInstanceState(estadoGuardado);
    	if(mp != null){
    		int pos = mp.getCurrentPosition();
    		estadoGuardado.putInt("posicion", pos);
    	}
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle estadoGuardado) {
    	super.onRestoreInstanceState(estadoGuardado);
    	if(estadoGuardado != null && mp != null){
    		int pos = estadoGuardado.getInt("posicion");
    		mp.seekTo(pos);
    	}
    }
    
    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data){
    	super.onActivityResult(requestCode, resultCode, data);
    	if (requestCode==1234 & resultCode==RESULT_OK & data!=null){
    		int puntuacion = data.getExtras().getInt("puntuacion");
    		String nombre = "Yo";
    		//Mejor leerlo desde un Dialog o una nueva actividad
    		//AlertDialog.Builder
    		
    		almacen.guardarPuntuacion(puntuacion, nombre, System.currentTimeMillis());
    		lanzarPuntuaciones(null);
    	}
    }
}