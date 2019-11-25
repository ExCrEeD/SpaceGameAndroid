package co.devbeerloper.myicecreamgame;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.constraint.solver.widgets.Rectangle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

public class GameSurfaceView extends SurfaceView implements Runnable {

    private boolean isPlaying;
    private SpaceShip spaceShip;
    private List<Asteroid> asteroids  = new ArrayList<Asteroid>();
    private List<EnemySpaceShip> enemySpaceShips  = new ArrayList<EnemySpaceShip>();
    private List<Bullet> bullets  = new ArrayList<Bullet>();
    private List<BulletEnemy> enemyBullets  = new ArrayList<BulletEnemy>();
    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder holder;
    private Thread gameplayThread = null;
    private int Score=0;
    private boolean timeCharge = true;
    private Context context;
    float screenWith;
    float screenHeight;
    private int timeBetweenBullet = 0;

    /**
     * Contructor
     * @param context
     */
    public GameSurfaceView(Context context, float screenWith, float screenHeight) {
        super(context);
        this.context = context;
        this.screenHeight = screenHeight;
        this.screenWith = screenWith;
        spaceShip = new SpaceShip(context, screenWith, screenHeight);
        asteroids.add(new Asteroid(context,screenWith,screenHeight));
        asteroids.add(new Asteroid(context,3500,200,screenWith,screenHeight));
        asteroids.add(new Asteroid(context,4500,600,screenWith,screenHeight));
        enemySpaceShips.add(new EnemySpaceShip(context,4500,600,screenWith,screenHeight));
        enemySpaceShips.add(new EnemySpaceShip(context,3500,200,screenWith,screenHeight));
        enemySpaceShips.add(new EnemySpaceShip(context,6000,900,screenWith,screenHeight));

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
           timeBetweenBullet++;
           spaceShip.updateInfo();
           Rect spaceShipRec = new Rect((int)spaceShip.getPositionX(),(int)spaceShip.getPositionY(),
                   (int)spaceShip.getPositionX()+spaceShip.getSpriteSizeWidth(),(int)spaceShip.getPositionY()+spaceShip.getSpriteSizeHeigth());

           if(timeBetweenBullet == 60)
           {
                bullets.add(new Bullet(context,spaceShip.getPositionX(),spaceShip.getPositionY(),screenWith,screenHeight));
               timeBetweenBullet=0;
           }
           for(Bullet bullet:bullets) {
               bullet.updateInfo();
               Rect bulletRec = new Rect((int)bullet.getPositionX(),(int)bullet.getPositionY(),
                       (int)bullet.getPositionX()+bullet.getSpriteSizeWidth(),(int)bullet.getPositionY()+bullet.getSpriteSizeHeigth());
               for(Asteroid asteroid:asteroids)
               {
                   Rect asteroidRec = new Rect((int)asteroid.getPositionX(),(int)asteroid.getPositionY(),
                           (int)asteroid.getPositionX()+asteroid.getSpriteSizeWidth(),(int)asteroid.getPositionY()+asteroid.getSpriteSizeHeigth());

                   if(Rect.intersects(asteroidRec, bulletRec) )
                   {
                       asteroid.setPositionX(asteroid.getInitX());
                   }
               }
               for(EnemySpaceShip enemySpaceShip:enemySpaceShips)
               {
                   Rect enemySpaceShipRect = new Rect((int)enemySpaceShip.getPositionX(),(int)enemySpaceShip.getPositionY(),
                           (int)enemySpaceShip.getPositionX()+enemySpaceShip.getSpriteSizeWidth(),(int)enemySpaceShip.getPositionY()+enemySpaceShip.getSpriteSizeHeigth());
                   if(Rect.intersects(enemySpaceShipRect, bulletRec))
                   {
                       enemySpaceShip.setPositionX(enemySpaceShip.getInitX());
                   }
               }
           }

           for(BulletEnemy bullet:enemyBullets) {
               bullet.updateInfo();
               Rect bulletRec = new Rect((int)bullet.getPositionX(),(int)bullet.getPositionY(),
                       (int)bullet.getPositionX()+bullet.getSpriteSizeWidth(),(int)bullet.getPositionY()+bullet.getSpriteSizeHeigth());

               if(Rect.intersects(bulletRec, spaceShipRec))
               {
                   spaceShip.setLifePoints(spaceShip.getLifePoints() - 15);
                   bullet.setPositionX(-100);
               }
           }
           for(Asteroid asteroid:asteroids)
           {
               asteroid.updateInfo();
               Rect asteroidRec = new Rect((int)asteroid.getPositionX(),(int)asteroid.getPositionY(),
                       (int)asteroid.getPositionX()+asteroid.getSpriteSizeWidth(),(int)asteroid.getPositionY()+asteroid.getSpriteSizeHeigth());

               if(Rect.intersects(asteroidRec, spaceShipRec) || asteroid.getPositionX()<=0 )
               {
                   spaceShip.setLifePoints(spaceShip.getLifePoints() - 15);
                   asteroid.setPositionX(asteroid.getInitX());
               }
           }
           for(EnemySpaceShip enemySpaceShip:enemySpaceShips)
           {
               enemySpaceShip.updateInfo();
               enemySpaceShip.setTimeBetweenBullet(enemySpaceShip.getTimeBetweenBullet()+1);
               if(enemySpaceShip.getTimeBetweenBullet() ==200)
               {
                   enemyBullets.add(new BulletEnemy(context,enemySpaceShip.getPositionX(),enemySpaceShip.getPositionY(),screenWith,screenHeight));
                   enemySpaceShip.setTimeBetweenBullet(0);
               }
               Rect enemySpaceShipRect = new Rect((int)enemySpaceShip.getPositionX(),(int)enemySpaceShip.getPositionY(),
                       (int)enemySpaceShip.getPositionX()+enemySpaceShip.getSpriteSizeWidth(),(int)enemySpaceShip.getPositionY()+enemySpaceShip.getSpriteSizeHeigth());
               if(Rect.intersects(enemySpaceShipRect, spaceShipRec)|| enemySpaceShip.getPositionX()<=0)
               {
                   spaceShip.setLifePoints(spaceShip.getLifePoints() - 15);
                   enemySpaceShip.setPositionX(enemySpaceShip.getInitX());
               }
           }
           if(spaceShip.getLifePoints()<=0)
               isPlaying = false;

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
            for(Asteroid asteroid:asteroids)
            {
                canvas.drawBitmap(asteroid.getSpriteAsteroid(),asteroid.getPositionX(),asteroid.getPositionY(),paint);
            }
            for(EnemySpaceShip enemySpaceShip:enemySpaceShips)
            {
                canvas.drawBitmap(enemySpaceShip.getSpriteEnemySpaceShip(),enemySpaceShip.getPositionX(),enemySpaceShip.getPositionY(),paint);
            }
            for(Bullet bullet:bullets) {
                canvas.drawBitmap(bullet.getSpriteBullet(),bullet.getPositionX(),bullet.getPositionY(),paint);
            }
            for(BulletEnemy bullet:enemyBullets) {
                canvas.drawBitmap(bullet.getSpriteBullet(),bullet.getPositionX(),bullet.getPositionY(),paint);
            }
            if(timeCharge)
            {
                timeCharge = false;
                spaceShip.setLifePoints(100);
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
