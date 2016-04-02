package com.google.engedu.puzzle8;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Random;

public class PuzzleBoardView extends View {
    public static final int NUM_SHUFFLE_STEPS = 40;
    private Activity activity;
    private PuzzleBoard puzzleBoard;
    private ArrayList<PuzzleBoard> animation;
    private Random random = new Random();

    public PuzzleBoardView(Context context) {
        super(context);
        activity = (Activity) context;
        animation = null;
    }

    public void initialize(Bitmap imageBitmap, View parent) {
        int width = getWidth();
        //Log.d("buggy","my phone thinks its " + width + " pixels wide lol");
        width = 1000;
        puzzleBoard = new PuzzleBoard(imageBitmap, width);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (puzzleBoard != null) {
            if (animation != null && animation.size() > 0) {
                puzzleBoard = animation.remove(0);
                puzzleBoard.draw(canvas);
                if (animation.size() == 0) {
                    animation = null;
                    puzzleBoard.reset();
                    Toast toast = Toast.makeText(activity, "Solved! ", Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    this.postInvalidateDelayed(500);
                }
            } else {
                puzzleBoard.draw(canvas);
            }
        }
    }

    public void shuffle() {
        if (animation == null && puzzleBoard != null) {
            Random shuffles = new Random();
            PuzzleBoard curBoard = new PuzzleBoard(puzzleBoard);

            for(int i=0; i<NUM_SHUFFLE_STEPS; i++){
                ArrayList<PuzzleBoard> possibleMoves = curBoard.neighbours();
                int moveSelection = shuffles.nextInt(possibleMoves.size());
                curBoard = possibleMoves.get(moveSelection);
            }
            puzzleBoard=curBoard;
            invalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (animation == null && puzzleBoard != null) {
            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (puzzleBoard.click(event.getX(), event.getY())) {
                        invalidate();
                        if (puzzleBoard.resolved()) {
                            Toast toast = Toast.makeText(activity, "Congratulations!", Toast.LENGTH_LONG);
                            toast.show();
                        }
                        return true;
                    }
            }
        }
        return super.onTouchEvent(event);
    }

    /*public void solve() {
        puzzleBoard.reset();
        int fu = puzzleBoard.priority();
        Log.d("buggy",""+fu);
    }*/
    public void solve() {
        //Log.d("buggy","solving");
        PriorityQueue<PuzzleBoard> solutionQueue = new PriorityQueue<PuzzleBoard>(1, new PuzzleComparator());
        puzzleBoard.reset();
        solutionQueue.add(puzzleBoard);

        while(!solutionQueue.isEmpty()){
            //Log.d("buggy","Lookig for solution");
            PuzzleBoard currBoard = solutionQueue.poll();
            int purePrio = currBoard.priority()-currBoard.getSteps();
            //Log.d("buggy","Priority = " + shit);
            //Log.d("buggy","Wait what");
            if(purePrio == 0){
                //Log.d("buggy","IS THIS REACHED?");
                //create an ArrayList of all PuzzleBoards leading to this solution by getting
                //PuzzleBoard.previousBoard then use Collections.reverse to turn it into
                //an in-order sequence of all the steps to solving the puzzle. If you copy that
                //ArrayList to PuzzleBoardView.animation the given implementation of onDraw will
                //sequence of steps to solve the puzzle

                ArrayList<PuzzleBoard> pathToVic = currBoard.getPreviousBoards();
                Log.d("buggy","ever finished?");
                Collections.reverse(pathToVic);
                animation = pathToVic;
                //solutionQueue.clear();
                invalidate();
                return;
            }

            ArrayList<PuzzleBoard> solutions = currBoard.neighbours();
            for (int i = 0; i<solutions.size(); i++){

                solutionQueue.add(solutions.get(i));
            }
            //for (PuzzleBoard board:solutions) {
                //solutionQueue.add(board);
            //}
        }
        Log.d("buggy","break out of while");
    }

    class PuzzleComparator implements Comparator<PuzzleBoard>{
        @Override
        public int compare(PuzzleBoard lhs, PuzzleBoard rhs) {
            //Log.d("buggy","comparing");
            return lhs.priority() - rhs.priority();
        }
    }

}
