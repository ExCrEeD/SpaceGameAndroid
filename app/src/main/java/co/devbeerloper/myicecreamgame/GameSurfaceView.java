package co.devbeerloper.myicecreamgame;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

public class GameSurfaceView extends SurfaceView implements Runnable {

    private boolean isPlaying;
    private SpaceShip spaceShip;
    private List<Asteroid> asteroids  = new ArrayList<Asteroid>();
    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder holder;
    private Thread gameplayThread = null;
    private int Score=0;
    private boolean timeCharge = true;

    /**
     * Contructor
     * @param context
     */
    public GameSurfaceView(Context context, float screenWith, float screenHeight) {
        super(context);
        spaceShip = new SpaceShip(context, screenWith, screenHeight);
        asteroids.add(new Asteroid(context,screenWith,screenHeight));
        asteroids.add(new Asteroid(context,screenWith,screenHeight));
        paint = new Paint();
        holder = getHolder();
        isPlaying = true;
    }

    /**
     * Method implemented from runnable interface
     */
    @Override
    public void run() {
        while (isPlaying) {
            paintFrame();
            updateInfo();
        }
        gameOver();
    }

    private void updateInfo() {
       if(!timeCharge) {
           spaceShip.updateInfo();
           for(Asteroid asteroid:asteroids)
           {
               asteroid.updateInfo();
           }
//           canvas.drawText("s", spaceShip.getPositionX()+spaceShip.getSpriteSizeWidth(), spaceShip.getPositionY(), paint);
//           canvas.drawText("s1", spaceShip.getPositionX()+spaceShip.getSpriteSizeHeigth(), spaceShip.getPositionY()+spaceShip.getSpriteSizeWidth(), paint);
//           canvas.drawText("a", asteroid.getPositionX(), asteroid.getPositionY(), paint);
//           canvas.drawText("a1", asteroid.getPositionX(), asteroid.getPositionY()+asteroid.getSpriteSizeHeigth(), paint);
//
//
//           if ((asteroid.getPositionX() >= spaceShip.getPositionX() && asteroid.getPositionX() <= spaceShip.getSpriteSizeWidth()
//           )&& (asteroid.getPositionY() >= spaceShip.getPositionY() && asteroid.getPositionY() <= spaceShip.getSpriteSizeHeigth())) {
//               spaceShip.setLifePoints(spaceShip.getLifePoints() - 15);
//               asteroid.setPositionX(Asteroid.getInitX());
//               if(spaceShip.getLifePoints()<=0)
//               {
//                   isPlaying = false;
//               }
//           }
       }
    }

    private void paintFrame() {
        if (holder.getSurface().isValid()){

            canvas = holder.lockCanvas();
            canvas.drawBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.spacewallpaper),0,0,null);
            canvas.drawBitmap(spaceShip.getSpriteSpaceShip(),spaceShip.getPositionX(),spaceShip.getPositionY(),paint);

            paint.setColor(Color.WHITE);
            paint.setStyle(Paint.Style.FILL);
            paint.setTextSize(75);
            canvas.drawText("Life Points: "+spaceShip.getLifePoints(), 10, 60, paint);
            paint.setColor(Color.BLACK);
            paint.setTextSize(50);
            canvas.drawText("s", spaceShip.getPositionX()+spaceShip.getSpriteSizeWidth(), spaceShip.getPositionY(), paint);
            canvas.drawText("s1", spaceShip.getPositionX()+spaceShip.getSpriteSizeHeigth(), spaceShip.getPositionY()+spaceShip.getSpriteSizeWidth(), paint);
            for(Asteroid asteroid:asteroids)
            {
                canvas.drawBitmap(asteroid.getSpriteAsteroid(),asteroid.getPositionX(),asteroid.getPositionY(),paint);
                canvas.drawText("a", asteroid.getPositionX(), asteroid.getPositionY(), paint);
                canvas.drawText("a1", asteroid.getPositionX(), asteroid.getPositionY()+asteroid.getSpriteSizeHeigth(), paint);
            }
            if(timeCharge)
            {
                timeCharge = false;
                spaceShip.setLifePoints(100);
                //asteroid.setPositionX(1500);
            }
            holder.unlockCanvasAndPost(canvas);
        }
    }

    private void gameOver(){
        canvas = holder.lockCanvas();
        canvas.drawBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.gameover),0,0,null);
        holder.unlockCanvasAndPost(canvas);
    }
    public void pause() {
        isPlaying = false;
        try {
            gameplayThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    public void resume() {

        isPlaying = true;
        gameplayThread = new Thread(this);
        gameplayThread.start();
    }

    /**
     * Detect the action of the touch event
     * @param motionEvent
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                System.out.println("TOUCH UP - STOP JUMPING");
                spaceShip.setJumping(false);
                break;
            case MotionEvent.ACTION_DOWN:
                System.out.println("TOUCH DOWN - JUMP");
                spaceShip.setJumping(true);
                break;
        }
        return true;
    }

}
