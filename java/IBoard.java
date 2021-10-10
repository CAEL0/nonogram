public interface IBoard {
    int getWidth();
    int getHeight();

    int[] getColumnConstraints(int r);
    int[] getRowConstraints(int c);

    void fill(int r, int c);
    void erase(int r, int c);
    CellState getCellState(int r, int c);
}
