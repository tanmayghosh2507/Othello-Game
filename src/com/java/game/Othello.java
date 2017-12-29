package com.java.game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class Othello {
	
	static char[][] board = new char[8][8];

	public static void main(String[] args) throws IOException {
		
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);
		
		for (int i=0; i<8; i++) {
			for (int j=0; j<8; j++) {
				if((i==3 && j==3) || (i==4 && j==4)) {
					board[i][j] = 'w';
				} else if ((i==4 && j==3)||(i==3 && j==4)) {
					board[i][j] = 'b';
				} else {
					board[i][j]='.';
				}
			}
		}
		
		System.out.println("Initial Board Configuration");
		printBoard(board);
		
		System.out.print("Choose your color(b/w): ");
		char userColor = br.readLine().charAt(0);
		
		char computerColor;
		
		if(userColor == 'w')
			computerColor = 'b';
		else if(userColor == 'b')
			computerColor = 'w';
		else {
			System.out.println("Wrong color chosen. So, we'll start the game with default userColor=w, computerColor=b.");
			userColor = 'w';
			computerColor = 'b';
		}
		
		Boolean validMoveLeft = true;
		char[][] tempboard;
		
		//Q4(f). Repeating the process below until Game is over.
		while(!isGameOver(board, true) && validMoveLeft) {
			
			List<Coordinate> list = getValidMoves(board, userColor, computerColor);
			if(list.isEmpty()) {
				validMoveLeft = false;
				System.out.println("No Valid Move left for user. Computer Won.");
				continue;
			}
			System.out.print("List of possible moves for user("+userColor+") at this point: ");
			for(int i = 0; i<list.size(); i++) {
				System.out.print("("+list.get(i).getX()+", "+list.get(i).getY()+") ");
			}
			System.out.println();
			
			//Q4(b). Input a move from user as 2 integers representing the coordinate of the move.
			System.out.print("Enter your move in the form of row_number<space>column_number (Starting with 0, [0-7] [0-7]). eg: 2 3: ");
			String str = br.readLine();
			String[] input = str.split(" ");
			int row = Integer.parseInt(input[0]);
			int col = Integer.parseInt(input[1]);
			
			//Q4(c). Updating the board with the user's move.
			if((tempboard = takeAction(board, row, col, userColor, computerColor, true)) != null) {
				board = tempboard;
				System.out.println("Board Configuration after your move:");
				printBoard(board);
			} else {
				System.out.println("Invalid Move! Try Again...");
				continue;
			}
				
			Coordinate move = getComputerMove(board, computerColor);
			if(move.getX()==-1 || move.getY()==-1) {
				validMoveLeft = false;
				System.out.println("No Valid Move left for Computer. User Won.");
				continue;
			}
			
			//Q4(d). Printing the agent's move from alpha-beta search
			System.out.println("Computer's Move: "+move.getX() + " " + move.getY());
			
			//Q4(e). Updating the board with the agent's move
			printBoard(takeAction(board, move.getX(), move.getY(), computerColor, userColor, true));
		}
	}
	
	/*
	 * Q3. Move generator function for Othello which takes a state of the game as input and returns a list of legal moves at that state.
	 */
	private static List<Coordinate> getValidMoves(char[][] board, char forColor, char againstColor) {
		List<Coordinate> corList = new ArrayList<Coordinate>();
		for (int i=0; i<8; i++) {
			for(int j=0; j<8; j++) {
				if(board[i][j]=='.') {
					if(takeAction(board, i, j, forColor, againstColor, false) != null) {
						Coordinate cor = new Coordinate();
						cor.setX(i);
						cor.setY(j);
						corList.add(cor);
					}				
				}
			}
		}
		return corList;
	}
	
	/*
	 * Given a recent action from either user/computer in form of a row
	 * and column number, this method takes the action as per the game rules of Othello.
	 * It will return null if the move is invalid.
	 */
	private static char[][] takeAction(char[][] board, int startrow, int startcol, char forColor, char againstColor, Boolean change) {
		int row, col, count=0, temp;
		if(startrow>=8 || startrow<0 || startcol>=8 || startcol<0 || board[startrow][startcol]!='.') {
			return null;
		} else {
			
			//For the increasing row direction row++, downwards.
			row= startrow+1;
			col = startcol;
			temp = row;
			while(row<8 && row>=0 && col<8 && col>=0) {
				if(board[row][col]==againstColor) {
					row++;
				} else if(board[row][col]=='.') {
					break;
				} else if(board[row][col]==forColor){
					count += Math.abs(row-temp);
					if(change) {
						for(int i=temp; i<row; i++) {
							board[i][col] = forColor;
						}
					}
					break;
				}
			}
			
			//For the decreasing row direction row--, upwards.
			row= startrow-1;
			col = startcol;
			temp = row;
			while(row<8 && row>=0 && col<8 && col>=0) {
				if(board[row][col]==againstColor) {
					row--;
				} else if(board[row][col]=='.') {
					break;
				} else if(board[row][col]==forColor){
					count += Math.abs(row-temp);
					if(change) {
						for(int i=temp; i>row; i--) {
							board[i][col] = forColor;
						}
					}
					break;
				}
			}
			
			//For the increasing column direction col++, rightwards.
			row= startrow;
			col = startcol+1;
			temp = col;
			while(row<8 && row>=0 && col<8 && col>=0) {
				if(board[row][col]==againstColor) {
					col++;
				} else if(board[row][col]=='.') {
					break;
				} else if(board[row][col]==forColor){
					count += Math.abs(col-temp);
					if(change) {
						for(int i=temp; i<col; i++) {
							board[row][i] = forColor;
						}
					}
					break;
				}
			}
			
			//For the decreasing column direction col--, leftwards.
			row= startrow;
			col = startcol-1;
			temp = col;
			while(row<8 && row>=0 && col<8 && col>=0) {
				if(board[row][col]==againstColor) {
					col--;
				} else if(board[row][col]=='.') {
					break;
				} else if(board[row][col]==forColor){
					count += Math.abs(col-temp);
					if(change) {
						for(int i=temp; i>col; i--) {
							board[row][i] = forColor;
						}
					}
					break;
				}
			}
			
			//For the increasing row and column direction row++ col++, down-right diagonal.
			row= startrow+1;
			col = startcol+1;
			int temprow = row, tempcol = col;
			while(row<8 && row>=0 && col<8 && col>=0) {
				if(board[row][col]==againstColor) {
					row++;
					col++;
				} else if(board[row][col]=='.') {
					break;
				} else if(board[row][col]==forColor){
					count += Math.abs(row-temprow);
					if(change) {
						for(int i=temprow, j=tempcol; i<row && j<col; i++, j++) {
							board[i][j] = forColor;
						}
					}
					break;
				}
			}
			
			//For the increasing row and decreasing column direction row++ col--, down-left diagonal.
			row= startrow+1;
			col = startcol-1;
			temprow = row;
			tempcol = col;
			while(row<8 && row>=0 && col<8 && col>=0) {
				if(board[row][col]==againstColor) {
					row++;
					col--;
				} else if(board[row][col]=='.') {
					break;
				} else if(board[row][col]==forColor){
					count += Math.abs(row-temprow);
					if(change) {
						for(int i=temprow, j=tempcol; i<row && j>col; i++, j--) {
							board[i][j] = forColor;
						}
					}
					break;
				}
			}
			
			//For the decreasing row and increasing column direction row-- col++, up-right diagonal.
			row= startrow-1;
			col = startcol+1;
			temprow = row;
			tempcol = col;
			while(row<8 && row>=0 && col<8 && col>=0) {
				if(board[row][col]==againstColor) {
					row--;
					col++;
				} else if(board[row][col]=='.') {
					break;
				} else if(board[row][col]==forColor){
					count += Math.abs(row-temprow);
					if(change) {
						for(int i=temprow, j=tempcol; i>row && j<col; i--, j++) {
							board[i][j] = forColor;
						}
					}
					break;
				}
			}
			
			//For the decreasing row and column direction row-- col--, up-left diagonal.
			row= startrow-1;
			col = startcol-1;
			temprow = row;
			tempcol = col;
			while(row<8 && row>=0 && col<8 && col>=0) {
				if(board[row][col]==againstColor) {
					row--;
					col--;
				} else if(board[row][col]=='.') {
					break;
				} else if(board[row][col]==forColor){
					count += Math.abs(row-temprow);
					if(change) {
						for(int i=temprow, j=tempcol; i>row && j>col; i--, j--) {
							board[i][j] = forColor;
						}
					}
					break;
				}
			}
		}
		
		if(count>0) {
			if(change) {
				board[startrow][startcol] = forColor;
			}
			return board;
		}
		
		return null;
	}

	/*
	 * Q1. Method which takes the current state as input and returns a move to be made by the agent.
	 */
	private static Coordinate getComputerMove(char[][] board, char currentColor) {
		
		Coordinate move = new Coordinate();
		char opponentColor = 'b';
		if(currentColor == 'b') {
			opponentColor = 'w';
		}
		List<Coordinate> validMoves = getValidMoves(board, currentColor, opponentColor);
		if(validMoves.isEmpty()) {
			move.setX(-1);
			move.setY(-1);
		} else {
			int bestMoveVal = Integer.MIN_VALUE;
			int bestX = validMoves.get(0).getX(); 
			int bestY = validMoves.get(0).getY();
			for (int i=0; i<validMoves.size(); i++) {
				char[][] tempBoard = new char[8][8];
				copyBoard(board, tempBoard);
				takeAction(tempBoard, validMoves.get(i).getX(), validMoves.get(i).getY(), currentColor, opponentColor, true);
				int val = alphaBetaValue(tempBoard, currentColor, opponentColor, 1);
				if(val > bestMoveVal) {
					bestMoveVal = val;
					bestX = validMoves.get(i).getX();
					bestY = validMoves.get(i).getY();
				}
			}
			move.setX(bestX);
			move.setY(bestY);
		}
		return move;
	}
	
	/*
	 * Method to implement the alpha beta search algorithm. It is a recursive method to get the maximum evaluation value at a depth level of 2..
	 */
	private static int alphaBetaValue(char[][] tempBoard, char originalColor, char currentColor, int depth) {
		
		char opponentColor = 'b';
		if(currentColor == 'b') {
			opponentColor = 'w';
		}
		if((depth==2) || isGameOver(tempBoard, false)) {
			return getEvaluationValue(tempBoard, originalColor, opponentColor);
		}
		
		List<Coordinate> validMoves = getValidMoves(board, currentColor, opponentColor);
		if(validMoves.isEmpty()) {
			return alphaBetaValue(tempBoard, originalColor, opponentColor, depth+1);
		} else {
			int bestMoveVal = Integer.MIN_VALUE;
			if(originalColor != currentColor)
				bestMoveVal = Integer.MAX_VALUE;
			
			for (int i=0; i<validMoves.size(); i++) {
				char[][] tempBoard2 = new char[8][8];
				copyBoard(board, tempBoard2);
				takeAction(tempBoard2, validMoves.get(i).getX(), validMoves.get(i).getY(), currentColor, opponentColor, true);
				int val = alphaBetaValue(tempBoard2, originalColor, opponentColor, depth+1);
				if(originalColor == currentColor) {
					if(val > bestMoveVal) {
						bestMoveVal = val;
					}
				} else {
					if(val < bestMoveVal) {
						bestMoveVal = val;
					}
				}
			}
			return bestMoveVal;
		}
	}
	
	/*
	 * Utility method to make a copy of the current board
	 */
	private static void copyBoard(char[][] currentBoard, char[][] tempBoard) {
		for(int i=0; i<8; i++) {
			tempBoard[i] = currentBoard[i].clone();
		}
	}
	
	/*
	 * Q2. An evaluation function for Othello which takes a state of the game as input and returns the evaluation value.
	 */
	private static int getEvaluationValue(char[][] board, char forColor, char againstColor) {
		int forCount=0, againstCount=0;
		for (int i=0; i<8; i++) {
			for (int j=0; j<8; j++) {
				if(board[i][j]==forColor)
					forCount++;
				else if(board[i][j]==againstColor)
					againstCount++;
			}
		}
		return forCount - againstCount;
	}

	/*
	 * Q4(a). Simple utility method to print the game board in 2D array form.
	 */
	private static void printBoard(char[][] board) {
		for (int i=0; i<8; i++) {
			for (int j=0; j<8; j++) {
				System.out.print(board[i][j]+" ");
			}
			System.out.println("");
		}
	}
	
	/*
	 * Utility method for checking whether a game is over? A game is over if:
	 * 1. The board is filled
	 * 2. No valid move left for a player
	 */
	private static Boolean isGameOver(char[][] board, Boolean ifPrint) {
		
		int bCount=0, wCount=0;
		for (int i=0; i<8; i++) {
			for (int j=0; j<8; j++) {
				if(board[i][j]=='b')
					bCount++;
				else if(board[i][j]=='w')
					wCount++;
			}
		}
		
		if ((bCount + wCount) == 64) {
			if(bCount>wCount)
				if(ifPrint)
					System.out.println("Black Won!");
			else if (bCount == wCount)
				if(ifPrint)
					System.out.println("Tie!");
			else
				if(ifPrint)
					System.out.println("White Won!");
			return true;
		}
		return false;
	}

}

/*
 * Utility class for the coordinate of any point in board.
 */
class Coordinate{
	int x;
	int y;
	
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
}
