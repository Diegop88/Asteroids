package com.diegop.asteroides;

import java.util.Vector;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;
import android.graphics.drawable.shapes.RectShape;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

public class VistaJuego extends View{
	
	private ThreadJuego thread = new ThreadJuego();
	private static int PERIODO_PROCESO = 50;
	private long ultimoProceso = 0;
	
	boolean musica= true;
	boolean efectos= true;
	SoundPool soundPool;
	int idDisparo, idExplosion;
	
	private float mX = 0, mY = 0;
	//private boolean disparo = false;
	
	private Vector<Grafico> Asteroides;
	private Drawable drawableAsteroide[] = new Drawable[3];
	private int numAsteroides = 5;
	private int numFragmentos;
	
	private Grafico nave;
	private int giroNave;
	private float aceleracionNave;
	private static final int PASO_GIRO_NAVE = 5;
	private static final float PASO_ACELERACION_NAVE = 0.5f;
	
	private Vector<Grafico> Misiles;
	private Drawable drawableMisil;
	private static int PASO_VELOCIDAD_MISIL = 12;
	private Vector<Integer> tiempoMisiles;
	
	private int puntuacion = 0;
	
	private Activity padre;
		
	public VistaJuego(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		Drawable drawableNave;
		
		SharedPreferences pref = context.getSharedPreferences("com.diegop.asteroides_preferences", Context.MODE_PRIVATE);
		
		efectos = pref.getBoolean("efectos", true);
		
		if(efectos){
			soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
			idDisparo = soundPool.load(context, R.raw.disparo, 0);
			idExplosion = soundPool.load(context, R.raw.explosion, 0);
		}
		
		numFragmentos = Integer.parseInt(pref.getString("fragmentos", "3"));
		
		if(pref.getString("graficos", "0").equals("0")) {
			Path pathNave = new Path();
			pathNave.moveTo((float)  0,(float) 0);
			pathNave.lineTo((float)  0,(float) 16);
			pathNave.lineTo((float) 20,(float) 8);
			pathNave.lineTo((float)  0,(float) 0);
			ShapeDrawable dNave = new ShapeDrawable(new PathShape(pathNave, 20, 16));
			dNave.getPaint().setColor(Color.BLUE);
			dNave.getPaint().setStyle(Style.STROKE);
			dNave.setIntrinsicWidth(20);
			dNave.setIntrinsicHeight(16);
			drawableNave = dNave;

			ShapeDrawable dMisil = new ShapeDrawable(new RectShape());
			dMisil.getPaint().setColor(Color.RED);
			dMisil.getPaint().setStyle(Style.STROKE);
			dMisil.setIntrinsicWidth(15);
			dMisil.setIntrinsicHeight(3);
			drawableMisil = dMisil;

			Path pathAsteroide = new Path();
			pathAsteroide.moveTo((float) 15,(float)  0);
			pathAsteroide.lineTo((float) 30,(float)  0);
			pathAsteroide.lineTo((float) 30,(float) 15);
			pathAsteroide.lineTo((float) 40,(float) 10);
			pathAsteroide.lineTo((float) 50,(float) 20);
			pathAsteroide.lineTo((float) 40,(float) 30);
			pathAsteroide.lineTo((float) 45,(float) 45);
			pathAsteroide.lineTo((float) 40,(float) 50);
			pathAsteroide.lineTo((float) 20,(float) 50);
			pathAsteroide.lineTo((float)  0,(float) 30);
			pathAsteroide.lineTo((float)  0,(float) 10);
			pathAsteroide.lineTo((float) 15,(float)  0);

			for(int i=0 ; i<3 ; i++){
			ShapeDrawable dAsteroide = new ShapeDrawable(new PathShape(pathAsteroide, 50, 50));
			dAsteroide.getPaint().setColor(Color.WHITE);
			dAsteroide.getPaint().setStyle(Style.STROKE);
			dAsteroide.setIntrinsicWidth(50 - (i * 14));
			dAsteroide.setIntrinsicHeight(50 - (i * 14));
			drawableAsteroide[i] = dAsteroide;
			}
			
			setBackgroundColor(Color.BLACK);
			
		} else {
			drawableNave = context.getResources().getDrawable(R.drawable.nave);
			drawableMisil = context.getResources().getDrawable(R.drawable.misil1);
			drawableAsteroide[0] = context.getResources().getDrawable(R.drawable.asteroide1);
			drawableAsteroide[1] = context.getResources().getDrawable(R.drawable.asteroide2);
			drawableAsteroide[2] = context.getResources().getDrawable(R.drawable.asteroide3);
		}
		
		
		nave = new Grafico(this, drawableNave);
		
		Misiles = new Vector<Grafico> ();
		tiempoMisiles = new Vector<Integer> ();
		
		Asteroides = new Vector<Grafico> ();
		for(int i = 0; i < numAsteroides; i++) {
			Grafico asteroide = new Grafico(this, drawableAsteroide[0]);
			asteroide.setIncX(Math.random() * 4 - 2);
			asteroide.setIncY(Math.random() * 4 - 2);
			asteroide.setAngulo((int) (Math.random() * 360));
			asteroide.setRotacion((int) (Math.random() * 8 - 4));
			Asteroides.add(asteroide);
		}
	}
	
	synchronized protected void actualizaFisica(){
				
		long ahora = System.currentTimeMillis();
		
		if (ultimoProceso + PERIODO_PROCESO > ahora) {
			return;
		}
		
		double retardo = (ahora - ultimoProceso) / PERIODO_PROCESO;
		ultimoProceso = ahora;
		
		nave.setAngulo((int) (nave.getAngulo() + giroNave * retardo));
		double nIncX = nave.getIncX() + aceleracionNave * Math.cos(Math.toRadians(nave.getAngulo())) * retardo;
		double nIncY = nave.getIncY() + aceleracionNave * Math.sin(Math.toRadians(nave.getAngulo())) * retardo;
		
		if(Math.hypot(nIncX, nIncY) <= Grafico.getMaxVelocidad()) {
			nave.setIncX(nIncX);
			nave.setIncY(nIncY);
		}
		
		nave.incrementaPos(retardo);
		
		if(Misiles.size() > 0) {			
			for(int j = 0; j < Misiles.size(); j++) {
				Misiles.elementAt(j).incrementaPos(retardo);
				tiempoMisiles.set(j, tiempoMisiles.get(j) -1);
				if (tiempoMisiles.get(j) < 0) {
					Misiles.remove(j);
					tiempoMisiles.remove(j);
				} else {
					for (int i = 0; i < Asteroides.size(); i++)
					{
						if(Misiles.elementAt(j).verificaColision(Asteroides.elementAt(i))) {
							destruyeAsteroide(i,j);
							break;
						}
					}
				}
			}
		}
	
		for(Grafico asteroide: Asteroides) {
			asteroide.incrementaPos(retardo);
		}
		
		for(Grafico asteroide: Asteroides) {
			if(asteroide.verificaColision(nave)) {
				salir();
			}
		}
	}
	
	private void destruyeAsteroide(int i, int j) {
		int tam;
		if(Asteroides.get(i).getDrawable() != drawableAsteroide[2]){
			if(Asteroides.get(i).getDrawable() == drawableAsteroide[1]){
				tam = 2;
			}
			else {
				tam = 1;
			}
			for(int n=0;n<numFragmentos;n++){
				Grafico asteroide = new Grafico(this, drawableAsteroide[tam]);
				asteroide.setPosX(Asteroides.get(i).getPosX());
				asteroide.setPosY(Asteroides.get(i).getPosY());
				asteroide.setIncX(Math.random() * 7 - 2 - tam);
				asteroide.setIncY(Math.random() * 7 - 2 - tam);
				asteroide.setAngulo((int) (Math.random() * 360));
				asteroide.setRotacion((int) (Math.random() * 8 - 4));
				Asteroides.add(asteroide);
			}
		}
		
		if(efectos){
			soundPool.play(idExplosion, 1, 1, 0, 0, 1);
		}
		
		Asteroides.remove(i);
		Misiles.remove(j);
		tiempoMisiles.remove(j);
		
		puntuacion += 1000;
		
		if (Asteroides.isEmpty()){
			salir();
		}
	}
	
	public void ActivaMisil() {
		Integer tiempoMisil;
		
		Grafico misil = new Grafico(this, drawableMisil);
		misil.setPosX(nave.getPosX() + nave.getAncho() / 2 - misil.getAncho() / 2);
		misil.setPosY(nave.getPosY() + nave.getAlto() / 2 - misil.getAlto() / 2);
		misil.setAngulo(nave.getAngulo());
		misil.setIncX(Math.cos(Math.toRadians(misil.getAngulo())) * PASO_VELOCIDAD_MISIL);
		misil.setIncY(Math.sin(Math.toRadians(misil.getAngulo())) * PASO_VELOCIDAD_MISIL);
		tiempoMisil = (int) Math.min(this.getWidth() / Math.abs(misil.getIncX()), this.getHeight() / Math.abs(misil.getIncY())) - 2;
		tiempoMisiles.add(tiempoMisil);
		Misiles.add(misil);
		
		if(efectos){
			soundPool.play(idDisparo, 1, 1, 1, 0, 1);
		}
	}
	
	class ThreadJuego extends Thread {
		private boolean pausa,corriendo;
		
		public synchronized void pausar() {
			pausa = true;
		}
		
		public synchronized void reanudar() {
			pausa = false;
			notify();
		}
		
		public void detener() {
			corriendo = false;
			if(pausa) reanudar();
		}
		
		@Override
		public void run() {
			corriendo = true;
			while (corriendo) {
				actualizaFisica();
				synchronized (this) {
					while (pausa) {
						try {
							wait();
						} catch (Exception e) {
							
						}
					}
				}
			}
		}
	}
	
	
	@Override
	protected void onSizeChanged(int ancho, int alto, int ancho_anter, int alto_anter) {
		super.onSizeChanged(ancho, alto, ancho_anter, alto_anter);
		
		nave.setPosX((ancho / 2) - (nave.getAncho() / 2));
		nave.setPosY((alto / 2) - (nave.getAlto() / 2));
		
		for(Grafico asteroide: Asteroides) {
			do {
				asteroide.setPosX(Math.random() * (ancho - asteroide.getAncho()));
				asteroide.setPosY(Math.random() * (alto - asteroide.getAlto()));
			} while(asteroide.distancia(nave) < (ancho + alto) / 5);
		}
		
		ultimoProceso = System.currentTimeMillis();
		thread.start();
	}
	
	@Override
	synchronized protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		nave.dibujaGrafico(canvas);
		
		for(Grafico misil: Misiles) {
			misil.dibujaGrafico(canvas);
		}
		
		for(Grafico asteroide: Asteroides) {
			asteroide.dibujaGrafico(canvas);
		}
	}
	
	@Override
	public boolean onKeyDown(int codigoTecla, KeyEvent evento) {
		super.onKeyDown(codigoTecla, evento);
		
		boolean procesada = true;
		switch(codigoTecla) {
		case KeyEvent.KEYCODE_DPAD_UP:
			aceleracionNave = +PASO_ACELERACION_NAVE;
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
			giroNave = - PASO_GIRO_NAVE;
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			giroNave = + PASO_GIRO_NAVE;
			break;
		case KeyEvent.KEYCODE_DPAD_CENTER:
		case KeyEvent.KEYCODE_ENTER:
			ActivaMisil();
			break;
		default:
			procesada = false;
			break;
		}		
		return procesada;
	}
	
	@Override
	public boolean onKeyUp(int codigoTecla, KeyEvent evento) {
		super.onKeyUp(codigoTecla, evento);
		
		boolean procesada = true;
		switch(codigoTecla){
		case KeyEvent.KEYCODE_DPAD_UP:
			aceleracionNave = 0;
			break;
		case KeyEvent.KEYCODE_DPAD_LEFT:
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			giroNave = 0;
			break;
		default:
			procesada = false;
			break;
		}
		return procesada;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event){
		super.onTouchEvent(event);
		
		float x = event.getX();
		float y = event.getY();
		switch(event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			//disparo = true;
			break;
		case MotionEvent.ACTION_MOVE:
			float dx = Math.abs(x - mX);
			float dy = Math.abs(y - mY);
			if(dy < 6 && dx > 6) {
				giroNave = Math.round((x - mX) / 2);
				//disparo = false;
			} else if (dx < 6 && dy > 6) {
				aceleracionNave = Math.round((mY - y) / 25);
				//disparo = false;
			}
			break;
		case MotionEvent.ACTION_UP:
			giroNave = 0;
			aceleracionNave = 0;
			//if(disparo) {
				//if(misilActivo == false)
					//ActivaMisil();
			//}
			break;
		}
		mX = x; mY = y;
		return true;
	}

	public ThreadJuego getThread() {
		return thread;
	}
	
	public void setPadre(Activity padre){
		this.padre = padre;
	}
	
	public void salir(){
		Bundle bundle = new Bundle();
		bundle.putInt("puntuacion", puntuacion);
		Intent intent = new Intent();
		intent.putExtras(bundle);
		padre.setResult(Activity.RESULT_OK, intent);
		padre.finish();
	}
}