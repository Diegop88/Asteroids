package com.diegop.asteroides;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class Juego extends Activity{
	
	private ImageView bdisparo;
	
	private VistaJuego vistaJuego;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.juego);
		vistaJuego = (VistaJuego) findViewById(R.id.VistaJuego);
		vistaJuego.setPadre(this);
		
		bdisparo = (ImageView) findViewById(R.id.botonDisparo);
		bdisparo.setOnClickListener(new OnClickListener(){
			public void onClick(View view){
				disparo(null);
			}
		});
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		vistaJuego.getThread().pausar();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		vistaJuego.getThread().reanudar();
	}
	
	@Override
	protected void onDestroy() {
		vistaJuego.getThread().detener();
		super.onDestroy();
	}
	
	
	public void disparo(View view){
		vistaJuego.ActivaMisil();
	}
	
}