package com.elmakers.mine.bukkit.plugins.persistence.dao;

// Enums don't need annotations to be persistable
public enum PermissionType
{
	DEFAULT,
	ALLOW_ALL,
	OPS_ONLY,
	PLAYER_ONLY
};