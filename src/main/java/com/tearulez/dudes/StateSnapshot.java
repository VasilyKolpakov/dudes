package com.tearulez.dudes;

import com.tearulez.dudes.model.Player;
import com.tearulez.dudes.model.Wall;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class StateSnapshot {
    private Player player;
    private List<Player> otherPlayers;
    private List<Wall> walls;
    private List<Point> bullets;

    private StateSnapshot() {
    }

    public static StateSnapshot create(Optional<Player> player,
                                       List<Player> otherPlayers,
                                       List<Wall> walls,
                                       List<Point> bullets) {
        StateSnapshot state = new StateSnapshot();
        state.player = player.orElse(null);
        state.otherPlayers = otherPlayers;
        state.walls = walls;
        state.bullets = bullets;
        return state;
    }

    static StateSnapshot empty() {
        return create(Optional.empty(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
    }

    public Optional<Player> getPlayer() {
        return Optional.ofNullable(player);
    }

    public List<Player> getOtherPlayers() {
        return otherPlayers;
    }

    public List<Wall> getWalls() {
        return walls;
    }

    public List<Point> getBullets() {
        return bullets;
    }
}