package fr.jc_android.spaceland;

public interface UniverseListener {

	void onUniverseProgress(int i, int nbsGalaxies);

	void onGalaxyProgress(int j, int nbsSolars);

	void onSolarProgress(int k, int nbsPlanets);

	void onPlanetProgress(int indexBlock, int nbsBlocks);

}
