package com.vortexwolf.chan.common.controls;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Movie;
import android.graphics.Paint;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class SimpleGifView extends View {
    private Movie movie;
    private long movieStart;
    private Matrix matrix;
    private float[] matrixValues = new float[9];
    Paint paint;
    
    public SimpleGifView(Context activity) {
        super(activity);
        init();
    }

    public SimpleGifView(Context activity, AttributeSet attbs) {
        super(activity, attbs);
        init();
    }

    public SimpleGifView(Context activity, AttributeSet attbs, int style) {
        super(activity, attbs, style);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
    }

    public boolean setData(byte[] array) {
        Movie movie = Movie.decodeByteArray(array, 0, array.length);

        return setMovie(movie);
    }
    
    public boolean setMovie(Movie movie) {
        return onMovieLoaded(movie);
    }

    private boolean onMovieLoaded(Movie movie) {
        if (movie != null) {
            if (movie.width() == 0) return false;
            this.movie = movie;
            invalidate();
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (movie == null) {
            super.onDraw(canvas);
            return;
        }
        
        canvas.drawColor(Color.TRANSPARENT);

        super.onDraw(canvas);

        long now = SystemClock.uptimeMillis();

        if (movieStart == 0) { // first time
            movieStart = now;
        }

        if (movie != null) {
            int dur = movie.duration();

            if (dur == 0) {
                dur = 1000;
            }

            int relTime = (int) ((now - movieStart) % dur);
            movie.setTime(relTime);

            canvas.save();
            
            if (matrix != null) {
                matrix.getValues(matrixValues);
                float scale = matrixValues[Matrix.MSCALE_X];
                canvas.translate(matrixValues[Matrix.MTRANS_X],matrixValues[Matrix.MTRANS_Y]);
                canvas.scale(matrixValues[Matrix.MSCALE_X], matrixValues[Matrix.MSCALE_Y]);
            } else {
                float width = (float) getWidth() / (float) movie.width();
                float height = (float) getHeight() / (float) movie.height();

                float scale = width > height ? height : width;
            
                int widthPixels = (int) (movie.width() * scale);
                int heightPixels = (int) (movie.height() * scale);

                canvas.translate((getWidth() - widthPixels) / 2, (getHeight() - heightPixels) / 2);

                canvas.scale(scale, scale);
            }
            

            movie.draw(canvas, 0, 0, paint);

            canvas.restore();

            invalidate();
        }
    }
    
    public void setImageMatrix(Matrix matrix) {
        this.matrix = matrix;
    }
}
