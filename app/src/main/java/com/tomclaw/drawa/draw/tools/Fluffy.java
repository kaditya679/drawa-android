package com.tomclaw.drawa.draw.tools;

import android.graphics.DiscretePathEffect;
import android.graphics.Paint;
import android.graphics.Path;

import org.androidannotations.annotations.EBean;

import java.util.Random;

/**
 * Created by solkin on 17.03.17.
 */
@EBean
public class Fluffy extends Tool {

    private static final int DOT_RADIUS = 6;

    private int startX, startY;
    private int prevX, prevY;
    private Path path;
    private Random random;

    Fluffy() {
    }

    @Override
    public void onInitialize() {
        this.path = new Path();
        this.random = new Random(System.currentTimeMillis());
    }

    @Override
    Paint initPaint() {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.MITER);
        paint.setStrokeCap(Paint.Cap.SQUARE);
        paint.setStrokeMiter(0.2f);
        paint.setPathEffect(new DiscretePathEffect(2, 2));
        return paint;
    }

    @Override
    int getAlpha() {
        return 0x20;
    }

    @Override
    public void onTouchDown(int x, int y) {
        startX = x;
        startY = y;
        path.moveTo(x, y);
        path.lineTo(x, y);

        prevX = x;
        prevY = y;

        drawPath(path);
    }

    @Override
    public void onTouchMove(int x, int y) {
        if (path.isEmpty()) {
            path.moveTo(prevX, prevY);
        }
        path.lineTo(x, y);

        prevX = x;
        prevY = y;

        drawPath(path);
    }

    @Override
    public void onTouchUp(int x, int y) {
        if (path.isEmpty()) {
            path.moveTo(prevX, prevY);
        }
        if (x == startX && y == startY) {
            for (int c = 0; c < 3; c++) {
                path.lineTo(randomizeCoordinate(x), randomizeCoordinate(y));
                drawPath(path);
            }
        } else {
            path.lineTo(x, y);
        }

        drawPath(path);

        prevX = 0;
        prevY = 0;
    }

    @Override
    public void onDraw() {
        path.reset();
    }

    @Override
    public byte getType() {
        return TYPE_FLUFFY;
    }

    private int randomizeCoordinate(int value) {
        return value + random.nextInt(DOT_RADIUS + 1) - DOT_RADIUS / 2;
    }
}