# Simulation Project : Dolphin Simulation

> Karim Bekhti, Kim-Tchoy Du, Gaétan Allaire

## Run the project

Run the Naive Model [here](src/NaiveModel/Simulation.java) and the Complex Model [here](src/ComplexModel/Simulation.java).

## Metrics visualization

The metrics are evaluated through 200 steps (can be changed in the [configuration file](parameters/configuration.ini)). Then the metrics are stored in CSV format in [naive_model_metrics.csv](naive_model_metrics.csv) and [complex_model_metrics.csv](complex_model_metrics.csv).

After running the simulation for both models, run the python [visualization script](data_visualizer.py) to see the results in graphs.