'''
Python script for visualizing the data of the simulation
'''

import pandas as pd
import matplotlib.pyplot as plt

nmm = pd.read_csv("naive_model_metrics.csv", sep=";")
cmm = pd.read_csv("complex_model_metrics.csv", sep=";")

# Total fish caught over each steps
plt.figure(figsize=(12, 6))
plt.plot(nmm["Step"], nmm["TotalFishCaught"], label="Naive Model")
plt.plot(cmm["Step"], cmm["TotalFishCaught"], label="Complex Model")
plt.xlabel("Step")
plt.ylabel("Total fish caught")
plt.title("Total fish caught over each steps")
plt.legend()
plt.show()

# Average fish caught per dolphins over each steps
plt.figure(figsize=(12, 6))
plt.plot(nmm["Step"], nmm["AverageFishCaughtPerDolphin"], label="Naive Model")
plt.plot(cmm["Step"], cmm["AverageFishCaughtPerDolphin"], label="Complex Model")
plt.xlabel("Step")
plt.ylabel("Fish caught per dolphin")
plt.title("Average fish caught per dolphins over each steps")
plt.legend()
plt.show()

# Average distance between dolphin and fish
plt.figure(figsize=(12, 6))
plt.plot(nmm["Step"], nmm["AverageDistanceToFish"], label="Naive Model")
plt.plot(cmm["Step"], cmm["AverageDistanceToFish"], label="Complex Model")
plt.xlabel("Step")
plt.ylabel("Distance")
plt.title("Average distance between dolphin and fish")
plt.legend()
plt.show()