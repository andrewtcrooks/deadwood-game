public interface GameActionListener {
    void onMove(String location);
    void onWork();
    void onEnd();
    void onSelectNeighbor(String location);
}
