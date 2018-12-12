import java.util.*;

import javax.lang.model.util.ElementScanner6;

public class AIAgent {
  Random rand;

  public AIAgent() {
    rand = new Random();
  }

  /*
    The method randomMove takes as input a stack of potential moves that the AI agent
    can make. The agent uses a rondom number generator to randomly select a move from
    the inputted Stack and returns this to the calling agent.
  */

  public Move randomMove(Stack possibilities) {
    int moveID = rand.nextInt(possibilities.size());
    System.out.println("Agent randomly selected move : " + moveID);
    for (int i = 1; i < (possibilities.size() - (moveID)); i++) {
      possibilities.pop();
    }
    Move selectedMove = (Move) possibilities.pop();
    return selectedMove;
  }

  public Move nextBestMove(Stack fWhite, Stack fBlack) {
    /*
    initilising needed variables / data structures
    */
    int blackWeight = 0;
    int piece = 0;
    Move captureMove;
    captureMove = null;
    Stack bestMoves = new Stack();
    Stack white = (Stack) fWhite.clone();
    Stack black = (Stack) fBlack.clone();
    /*
    The next best approach I have taken is a greedy best, in the sense the strategy is to capture black pieces .
    I havent tried to implement different strategies.
    this function looks for black pieces that can be captured
    each piece has been assigned a vlaue / weight
    if a white piece can capture two pieces it will choose the one with more value --> Queen over Pawn etc.....
    */
    for (int w = 0; w < white.size(); w++) {
      Move wm = (Move) white.get(w);
      // System.out.println("White Piece Name: "+wm.getStart().pieceName + "\nStarting Coordinate: " + wm.getStart().xCoor + ";" + wm.getStart().yCoor + "\n Landing Coordinates: "+wm.getLanding().xCoor +";"+wm.getLanding().yCoor);
      for (int b = 0; b < black.size(); b++) {
        Square bs = (Square) black.get(b);
        // System.out.println("Black Piece: "+bs.getName()+"Coordinates: "+bs.getXC()+";"+bs.getYC());
        if ((wm.getLanding().xCoor == bs.getXC()) && (wm.getLanding().yCoor == bs.getYC())) {
          bestMoves.push(wm);
        }
        while (!bestMoves.isEmpty()) {
          Move tmpBM = (Move) bestMoves.pop();
          System.out.println("Best Moves Found: " + tmpBM.getStart().pieceName +
            "\nLanding Coordinates: " + "[" + tmpBM.getLanding().xCoor + ";" + tmpBM.getLanding().yCoor + "]" +
            "\nCapture Piece: " + bs.getName());
          if (bs.getName().contains("Pawn")) {
            blackWeight = 1;
          } else if (bs.getName().contains("Bishop") || bs.getName().contains("Knight")) {
            blackWeight = 3;
          } else if (bs.getName().contains("Rook")) {
            blackWeight = 5;
          } else if (bs.getName().contains("Queen")) {
            blackWeight = 9;
          } else if (bs.getName().contains("King")) {
            blackWeight = Integer.MAX_VALUE; //used so that AI knows this is = game end, biggest 32 bit number is the blackWeight og the king
          }
          if (piece < blackWeight) {
            System.out.println("Best Move Score: " + blackWeight);
            piece = blackWeight;
            captureMove = tmpBM;
          }
        }
      }
    }
    if (piece > 0) {
      return captureMove;
    } else {
      return randomMove(white);
    }
  }

  public Move twoLevelsDeep(Stack whiteSquare, Stack whiteMoves, Stack blackSquares, Stack blackMoves) {
    /*
     initilising needed variables / data structures
     */
    int score = 0;
    int tempScore = 0;
    Move move, safeMove, captureMove;
    captureMove = null;
    move = null;
    Stack safeSqaures = new Stack();
    Stack secondLevelDeep = new Stack();
    Stack capturedMoves = new Stack();
    Stack whitePieces = (Stack) whiteSquare.clone();
    Stack blackPieces = (Stack) blackSquares.clone();
    Stack whitePossibleMoves = (Stack) whiteMoves.clone();
    Stack blackPossibleMoves = (Stack) blackMoves.clone();

    // Check all black moves
    // System.out.println("==================== BLACK MOVEMENTS ====================");
    // for (int t = 0; t < blackPossibleMoves.size(); t++) {
    //   Move tm = (Move) blackPossibleMoves.get(t);
    //   System.out.println("Move: " + tm.getStart().pieceName + " start [" + tm.getStart().xCoor + ";" + tm.getStart().yCoor + "] landing : [" + tm.getLanding().xCoor + ";" + tm.getLanding().yCoor + "]");
    // }
    // System.out.println("============================================================");

    //Get white possible moves
    // System.out.println("WHITE POSSIBLE MOVE : " + whitePossibleMoves.size());
    // System.out.println("BLACK POSSIBLE MOVE : " + blackPossibleMoves.size());
    // for(int t = 0;t<blackPossibleMoves.size();t++){
    //   Move bm = (Move) blackPossibleMoves.get(t);
    //   System.out.println("Move : "+bm.getStart().pieceName +" Coordinates: start " + bm.getStart().xCoor + ";"+bm.getStart().yCoor + " landing " +bm.getLanding().xCoor + ";" + bm.getLanding().yCoor);
    // }

    for (int wm = 0; wm < whitePossibleMoves.size(); wm++) {
      secondLevelDeep.clear();
      Move tempWhiteMove = (Move) whitePossibleMoves.get(wm);
      Square testSquare = new Square(tempWhiteMove.landing.xCoor, tempWhiteMove.landing.yCoor, tempWhiteMove.landing.pieceName);
      // Changing the board state
      for (int sld = 0; sld < whitePieces.size(); sld++) {
        Square currentState = (Square) whitePieces.get(sld);
        if (currentState.xCoor == tempWhiteMove.getStart().xCoor && currentState.yCoor == tempWhiteMove.getStart().yCoor) {
          secondLevelDeep.push(testSquare);
        } else {
          secondLevelDeep.push(currentState);
        }
      }
      for (int sl = 0; sl < secondLevelDeep.size(); sl++) {
        Square checkSquare = (Square) secondLevelDeep.get(sl);
        // Checking for safe square that AI can move Into
        for (int bm = 0; bm < blackPossibleMoves.size(); bm++) {
          // Check if black can take the AI piece
          Move tempBlackMove = (Move) blackPossibleMoves.get(bm);
          if (tempBlackMove.getLanding().xCoor == checkSquare.xCoor && tempBlackMove.getLanding().yCoor == checkSquare.yCoor) {

            System.out.println("White Piece Taken: " + checkSquare.pieceName);
            System.out.println("Black can take the piece Piece Name: " + tempBlackMove.getLanding().pieceName + " Coordinates: [ " + tempBlackMove.getLanding().xCoor + ";" + tempBlackMove.getLanding().yCoor + "]");

          }
        }
      }

      //Loop for black pieces for checking which pieces may be captured
      for (int bp = 0; bp < blackPieces.size(); bp++) {
        Square black = (Square) blackPieces.get(bp);
        // If white have the opportunity to take the black piece
        if (testSquare.xCoor == black.xCoor && testSquare.yCoor == black.yCoor) {
          //Check the next state based on the new SQUARE
          capturedMoves.push(tempWhiteMove);
        }
      }
    }
    return randomMove(whitePossibleMoves);
  }
}