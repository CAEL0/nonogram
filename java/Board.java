public class Board implements IBoard {
    private int width;
    private int height;

    private int[][] xConds;
    private int[][] yConds;

    private CellState[][] cells;

    public Board(int width, int height, int[][] xConds, int[][] yConds) throws IllegalArgumentException {
        this.width = width;
        this.height = height;

        if (width == 0 || height == 0) throw new IllegalArgumentException();
        if (xConds.length != width) throw new IllegalArgumentException();
        if (yConds.length != height) throw new IllegalArgumentException();

        this.cells = new CellState[height][width];
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
                cells[y][x] = CellState.EMPTY;

        this.xConds = xConds.clone();
        this.yConds = yConds.clone();
    }

    public int getWidth() { return this.width; }

    public int getHeight() { return this.height; }

    public int[] getColumnConstraints(int c) { return this.xConds[c].clone(); }
    public int[] getRowConstraints(int r) { return this.yConds[r].clone(); }

    public void fill(int r, int c) { this.cells[r][c] = CellState.FILLED; }
    public void erase(int r, int c) { this.cells[r][c] = CellState.EMPTY; }

    public CellState getCellState(int r, int c) { return this.cells[r][c]; }
}
