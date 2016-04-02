package com.google.engedu.puzzle8;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;


public class PuzzleBoard {
    private int steps;
    private PuzzleBoard previousBoard;
    private static final int NUM_TILES = 3;
    private static final int[][] NEIGHBOUR_COORDS = {
            { -1, 0 },
            { 1, 0 },
            { 0, -1 },
            { 0, 1 }
    };
    private ArrayList<PuzzleTile> tiles;

    PuzzleBoard(Bitmap bitmap, int parentWidth) {
        tiles = new ArrayList<PuzzleTile>();
        Bitmap currentBitmap;
        Bitmap fullSize = Bitmap.createScaledBitmap(bitmap, parentWidth, parentWidth, false);
        int tileSize = parentWidth/NUM_TILES;
        Log.d("buggy","parentWidth" + Integer.toString(parentWidth));
        Log.d("buggy","number tiles" + Integer.toString(NUM_TILES));
        Log.d("buggy","tile width" +Integer.toString(tileSize));
        PuzzleTile newTile;
        int xStart = 0;
        int yStart = 0;
        for(yStart=0; yStart<NUM_TILES; yStart++){
            for (xStart=0; xStart<NUM_TILES; xStart++) {
                currentBitmap = Bitmap.createBitmap(fullSize, xStart * tileSize, yStart * tileSize, tileSize, tileSize);
                if(yStart*xStart< (NUM_TILES-1) * (NUM_TILES-1) )
                    newTile = new PuzzleTile(currentBitmap, XYtoIndex(xStart,yStart));
                else
                    newTile = null;
                tiles.add(newTile);
            }
        }
        steps = 0;
    }

    public ArrayList<PuzzleBoard> getPreviousBoards(){
        if(this.previousBoard == null){
            ArrayList<PuzzleBoard> base = new ArrayList<>();
            base.add(this);
            return base;
        }
        else{
            ArrayList<PuzzleBoard> temp = previousBoard.getPreviousBoards();
            temp.add(this);
            return temp;
        }
    }

    PuzzleBoard(PuzzleBoard otherBoard) {
        tiles = (ArrayList<PuzzleTile>) otherBoard.tiles.clone();
        steps = otherBoard.steps + 1;
        previousBoard = otherBoard;
    }

    public void reset() {
        // Nothing for now but you may have things to reset once you implement the solver.
        steps=0;
        previousBoard = null;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        return tiles.equals(((PuzzleBoard) o).tiles);
    }

    public void draw(Canvas canvas) {
        if (tiles == null) {
            return;
        }
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile != null) {
                tile.draw(canvas, i % NUM_TILES, i / NUM_TILES);
            }
        }
    }

    public boolean click(float x, float y) {
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile != null) {
                if (tile.isClicked(x, y, i % NUM_TILES, i / NUM_TILES)) {
                    return tryMoving(i % NUM_TILES, i / NUM_TILES);
                }
            }
        }
        return false;
    }

    public boolean resolved() {
        for (int i = 0; i < NUM_TILES * NUM_TILES - 1; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile == null || tile.getNumber() != i)
                return false;
        }
        return true;
    }

    private int XYtoIndex(int x, int y) {
        return x + y * NUM_TILES;
    }

    protected void swapTiles(int i, int j) {
        PuzzleTile temp = tiles.get(i);
        tiles.set(i, tiles.get(j));
        tiles.set(j, temp);
    }

    private boolean tryMoving(int tileX, int tileY) {
        for (int[] delta : NEIGHBOUR_COORDS) {
            int nullX = tileX + delta[0];
            int nullY = tileY + delta[1];
            if (nullX >= 0 && nullX < NUM_TILES && nullY >= 0 && nullY < NUM_TILES &&
                    tiles.get(XYtoIndex(nullX, nullY)) == null) {
                swapTiles(XYtoIndex(nullX, nullY), XYtoIndex(tileX, tileY));
                return true;
            }

        }
        return false;
    }

    public ArrayList<PuzzleBoard> neighbours() {
        int emptyTile = 8;
        int neighborTile;
        ArrayList<PuzzleBoard> boardList = new ArrayList<PuzzleBoard>();
        int i;
        for(i=0; i<9; i++){
            if(tiles.get(i) == null){
                emptyTile = i;
                break;
            }
        }
        int[] emptyCoords = {i/NUM_TILES , i%NUM_TILES};
        for(i=0; i<4; i++){
            int x = emptyCoords[0] + NEIGHBOUR_COORDS[i][0];
            int y = emptyCoords[1] + NEIGHBOUR_COORDS[i][1];
            //if it is a good neighbor
            if( x>=0 && x<NUM_TILES && y>=0 && y<NUM_TILES){
                PuzzleBoard newBoard = new PuzzleBoard(this);
                neighborTile = XYtoIndex(x,y);
                newBoard.swapTiles(emptyTile, neighborTile);
                boardList.add(newBoard);
            }
        }
        return boardList;
    }

    public int priority() {
        int myPriority= 0;
        for(int i =0; i<NUM_TILES*NUM_TILES; i++){
            if(tiles.get(i) != null){
                //Log.d("buggy", "index " + i);
                int home = tiles.get(i).getNumber();
                int xCurr = i/NUM_TILES;
                int yCurr = i%NUM_TILES;
                int xHome = home/NUM_TILES;
                int yHome = home%NUM_TILES;
                //Log.d("buggy", "home " + home);
                myPriority += (Math.abs(xCurr-xHome) + Math.abs(yCurr-yHome));
                //Log.d("buggy", "Current Priority " + Integer.toString(myPriority));
            }
        }
        int manhattanNumber = myPriority + steps;
        //Log.d("buggy", "Total priority " + manhattanNumber);
        return manhattanNumber;
    }

}
