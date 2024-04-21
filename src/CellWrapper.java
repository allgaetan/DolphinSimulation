import fr.emse.fayol.maqit.simulator.components.SituatedComponent;
import fr.emse.fayol.maqit.simulator.environment.Cell;

public class CellWrapper extends Cell {

    private Cell cell;

    public CellWrapper(Cell cell) {
        super();
        this.cell = cell;
    }

    void removeContent() {
        this.setContent(null);
    }
}
