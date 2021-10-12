public final class BoardSolver implements IBoardSolver {
	private int n, m;
	private int[][] blank;
	
	// fill the rth row to the first step
	private void fillFirstStep(IBoard board, int r) {
		int[] row = board.getRowConstraints(r);
		if (row.length > 0) {
			int idx = 0;
			for (int i = 0; i < row[0]; i++) {
				board.fill(r, idx);
				idx++;
			}
			for (int k = 1; k < row.length; k++) {
				blank[r][k] = 1;
				idx++;
				for (int j = 0; j < row[k]; j++) {
					board.fill(r, idx);
					idx++;
				}
			}
			blank[r][blank[r].length - 1] = m - idx;
		}
	}
	
	// determine whether the rth row is the last step
	private boolean isLastStep(IBoard board, int r) {
		int[] row = board.getRowConstraints(r);
		if (row.length > 0) {
			if (blank[r][blank[r].length - 1] > 0) { return false; }
			for (int k = 1; k < blank[r].length - 1; k++) {
				if (blank[r][k] != 1) { return false; }
			}
		}
		return true;
	}
	
	// change the rth row to the next step
	private void fillNextStep(IBoard board, int r) {
		int[] row = board.getRowConstraints(r);
		if (blank[r][blank[r].length - 1] > 0) {
			board.fill(r, m - blank[r][blank[r].length - 1]);
			board.erase(r, m - blank[r][blank[r].length - 1] - row[row.length - 1]);
			blank[r][blank[r].length - 1]--;
			blank[r][blank[r].length - 2]++;
		}
		else {
			int idx = m - row[row.length - 1];
			for (int k = blank[r].length - 2; k > 0; k--) {
				idx -= (blank[r][k] + row[k - 1]);
				if (blank[r][k] != 1) {
					blank[r][k - 1]++;
					board.erase(r, idx);
					idx += row[k - 1];
					board.fill(r, idx);
					for (int i = 0; i < row.length - k; i++) {
						idx++;
						board.erase(r, idx);
						blank[r][k + i] = 1;
						for (int j = 0; j < row[k + i]; j++) {
							idx++;
							board.fill(r, idx);
						}
					}
					blank[r][blank[r].length - 1] = m - idx - 1;
					int tmp = m - idx - 1;
					for (int i = 0; i < tmp; i++) {
						idx++;
						board.erase(r, idx);
					}
					break;
				}
			}
		}
	}
	
	// check the validness of the 0~rth rows
	public boolean check(IBoard board, int r) {
		for (int c = 0; c < m; c++) {
			int[] col = board.getColumnConstraints(c);
			int[] res = new int[n];
			int size = 1;
			for (int i = 0; i < r + 1; i++) {
				if (board.getCellState(i, c) == CellState.FILLED) {
					res[size - 1]++;
				}
				else if (res[size - 1] != 0) {
					res[size] = 0;
					size++;
				}
			}
			if (size - 1 > col.length) { return false; }
			for (int k = 0; k < size - 1; k++) {
				if (res[k] != col[k]) { return false; }
			}
			if (res[size - 1] > 0) {
				if (size - 1 == col.length) { return false; }
				if (res[size - 1] > col[size - 1]) { return false; }
			}
		}
		return true;
	}

    @Override
    public IBoard solve(IBoard board) {
        /*
         * Function input:
         *  + board: A board filled with CellState.EMPTY.
         * 
         * Job:
         *  Return an answer board.
         *  If there are more than one answer, you can choose one of them.
         *  If there is no answer, return null.
         *
         * IBoard has following methods:
         *  int getWidth(): Returns the width of the board.
         *  int getHeight(): Returns the height of the board.
         *
         *  int[] getColumnConstraints(int c): Returns constraints for column #c.
         *  int[] getRowConstraints(int r): Returns constraints for row #r.
         *
         *  void fill(int r, int c): Fill the cell (#r, #c)
         *  void erase(int r, int c): Erase the cell (#r, #c)
         *  CellState getCellState(int r, int c): 
         *    Returns the state of the cell (#r, #c)
         *     - CellState.FILLED: the cell is filled
         *     - CellState.EMPTY: the cell is empty
         *  
         */
		n = board.getHeight();
		m = board.getWidth();
		
		// check the validness of the constraints
		for (int r = 0; r < n; r++) {
			int[] row = board.getRowConstraints(r);
			int sum = 0, length = row.length;
			for (int k = 0; k < length; k++) {
				sum += row[k];
			}
			if (sum + length - 1 > m) { return null; }
		}
		for (int c = 0; c < m; c++) {
			int[] col = board.getColumnConstraints(c);
			int sum = 0, length = col.length;
			for (int k = 0; k < length; k++) {
				sum += col[k];
			}
			if (sum + length - 1 > n) { return null; }
		}
		
		// blank[r][k] : the consecutive empty cells to the left of the kth constraint of the rth row
		blank = new int[n][];
		for (int r = 0; r < n; r++) {
			blank[r] = new int[board.getRowConstraints(r).length+ 1];
		}
		
		// back : whether the backtracking is going on
		boolean back = false;
		
		// backtracking
		int r = 0;
		while (true) {
			if (r == n) {
				boolean flag = true;
				for (int c = 0; c < m; c++) {
					int[] col = board.getColumnConstraints(c);
					int res[] = new int[n];
					int size = 1;
					for (int i = 0; i < n; i++) {
						if (board.getCellState(i, c) == CellState.FILLED) {
							res[size - 1]++;
						}
						else if (res[size - 1] != 0) {
							res[size] = 0;
							size++;
						}
					}
					if (res[size - 1] == 0) {
						size--;
					}
					if (size == 0 && col.length > 0) {
						flag = false;
						break;
					}
					for (int k = 0; k < size; k++) {
						if (res[k] != col[k]) {
							flag = false;
							break;
						}
					}
					if (flag == false) { break; }
				}
				if (flag) { return board; }
				else {
					back = true;
					r--;
				}
			}
			if (r == -1) { return null; }
			int[] row = board.getRowConstraints(r);
			if (row.length == 0) {
				if (back) { r--; }
				else {
					if (check(board, r)) { r++; }
					else {
						back = true;
						r--;
					}
				}
			}
			else if (back) {
				if (isLastStep(board, r)) {
					for (int c = 0; c < m; c++) {
						board.erase(r, c);
					}
					for (int k = 0; k < row.length + 1; k++) {
						blank[r][k] = 0;
					}
					r--;
				}
				else {
					fillNextStep(board, r);
					if (check(board, r)) {
						back = false;
						r++;
					}
				}
			}
			else {
				fillFirstStep(board, r);
				if (check(board, r)) { r++; }
				else { back = true; }
			}
		}
    }
}
