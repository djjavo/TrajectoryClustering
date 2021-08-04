# Trajectory Clustering

This repository contains the Java source code for the implementations of GVE and STA used in https://dl.acm.org/doi/abs/10.1145/3281548.3281549

## Usage

- Check configuration parameters are correct in `config.properties` file
  - Remember to disable the `data.header` property if your data does not have a header.
  - Remeber to select the correct clustering algorithm
- Ensure you have an `trajectories.csv` in your data folder
- Run with `java GVE <path-to-data-folder> config.properties`
  - Ensure a trailing slash is present on the path to your data folder
- The output clusters are appended to the input data in `clusters.csv` in the original data folder.
