package com.brezho.world.domination.game;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "units")
public class Unit {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "unit_name")
    private String unitName;

    @Column(name = "dev_level")
    private double levelOfDevelopment;

    @Column(name = "dev_level_next")
    private double levelOfDevelopmentNext;

    @Column(name = "shield_pres")
    private boolean shieldPresence;

    @Column(name = "shield_order")
    private boolean shieldOrder;

    @Column(name = "destroyed")
    private boolean destroyed;

/*    @Column(name = "will_be_destroyed")
    private boolean willBeDestroyed;*/

    @Column(name = "num_bombs_rec")
    private int numBombsRec;

    // Constructors
    public Unit(String unitName,
                double levelOfDevelopment,
                double levelOfDevelopmentNext,
                boolean shieldPresence,
                boolean shieldOrder,
                boolean destroyed,
                //boolean willBeDestroyed,
                int numBombRec) {
        this.unitName = unitName;
        this.levelOfDevelopment = levelOfDevelopment;
        this.levelOfDevelopmentNext = levelOfDevelopmentNext;
        this.shieldPresence = shieldPresence;
        this.shieldOrder = shieldOrder;
        this.destroyed = destroyed;
        //this.willBeDestroyed = willBeDestroyed;
        this.numBombsRec = numBombRec;
    }

    // Методы
/*    public void setShieldOrder() {
        shieldOrder = true;
    }*/

    /*public void removeShield() {
        shieldPresenceNext = false;
    }*/

    public void increaseLevelOfDevelopment(double increment) {
        levelOfDevelopmentNext += increment;
        levelOfDevelopmentNext = Math.floor(levelOfDevelopmentNext * 100) / 100;
    }

/*    public void reduceLevelOfDevelopment(double decrement) {
        levelOfDevelopment -= decrement;
        if (levelOfDevelopment < 0) {
            levelOfDevelopment = 0;
        }
    }*/
    

    public void restoreUnit() {
        destroyed = false;
    }

    public void bombardUnit() {
        numBombsRec += 1;
    }

    public void unitUpdate() {
        if (!destroyed) {
            levelOfDevelopment = levelOfDevelopmentNext;
            if (shieldOrder) {
                shieldPresence = true;
            }
            if (numBombsRec >= 2) {
                levelOfDevelopment = 0;
                shieldPresence = false;
                destroyed = true;
            } else if (numBombsRec == 1) {
                if (shieldPresence) {
                    shieldPresence = false;
                } else {
                    levelOfDevelopment = 0;
                    destroyed = true;
                }
            }

            shieldOrder = false;
            levelOfDevelopmentNext = levelOfDevelopment;
        }
    }

    public void experiment() {
        unitName += "_Exp";
    }
}

