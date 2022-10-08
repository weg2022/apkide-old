package com.apkide.core.logging

enum class Level(levelName: String) {
	ALL("All"), CONFIG("Config"), FINE("Fine"), FINER("Finer"), FINEST("Finest"), INFO("Info"), OFF(
		"Off"
	),
	SEVERE("Severe"), WARNING("Warning");
	
	fun intValue(): Int {
		return ordinal
	}
	
	override fun toString(): String {
		return name
	}
}