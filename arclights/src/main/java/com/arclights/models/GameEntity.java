package com.arclights.models;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

public abstract class GameEntity {
    public enum AttackType {
        PHYSICAL,
        ARTS
    }

    private final DoubleProperty x = new SimpleDoubleProperty(this, "x");
    private final DoubleProperty y = new SimpleDoubleProperty(this, "y");
    private final DoubleProperty hp = new SimpleDoubleProperty(this, "hp");
    private final DoubleProperty maxHp = new SimpleDoubleProperty(this, "maxHp");
    private final DoubleProperty atk = new SimpleDoubleProperty(this, "atk");
    private final IntegerProperty blockCount = new SimpleIntegerProperty(this, "blockCount");
    private final ObjectProperty<AttackType> attackType = new SimpleObjectProperty<>(this, "attackType");
    private final DoubleProperty attackInterval = new SimpleDoubleProperty(this, "attackInterval");
    private final DoubleProperty resistance = new SimpleDoubleProperty(this, "resistance");
    private final DoubleProperty defense = new SimpleDoubleProperty(this, "defense");
    private final BooleanProperty isAlive = new SimpleBooleanProperty(this, "isAlive", true);
    private final BooleanProperty isGround = new SimpleBooleanProperty(this, "isGround", true);

    public GameEntity(double x, double y, double hp, double atk, int blockCount, AttackType attackType, double attackInterval, double resistance, double defense, boolean isAlive, boolean isGround) {
        this.x.set(x);
        this.y.set(y);
        this.maxHp.set(hp); 
        this.hp.set(hp);
        this.atk.set(atk);
        this.blockCount.set(blockCount);
        this.attackType.set(attackType);
        this.attackInterval.set(attackInterval);
        this.resistance.set(resistance);
        this.defense.set(defense);
        this.isAlive.set(isAlive);
        this.isGround.set(isGround);
    }

    public abstract void update();

    public void takeDamage(double damage, AttackType attackType) {
        if (!isAlive()) return;

        double mitigation = (attackType == AttackType.PHYSICAL ? getDefense() : getResistance());
        double remainingHp = getHp();

        if (attackType == AttackType.ARTS) {
            remainingHp -= damage * (1 - mitigation);
        }
        else if (attackType == AttackType.PHYSICAL) {
            remainingHp -= Math.max(damage - mitigation, 0);
        }
        
        setHp(remainingHp);
    }

    // Getters and Setters
    public double getX() {return x.get();}
    public double getY() {return y.get();}
    public double getHp() {return hp.get();}
    public double getMaxHp() {return maxHp.get();}
    public double getAtk() {return atk.get();}
    public double getAttackInterval() {return attackInterval.get();}
    public double getDefense() {return defense.get();}
    public double getResistance() {return resistance.get();}
    public int getBlockCount() {return blockCount.get();}
    public AttackType getAttackType() {return attackType.get();}
    public boolean isAlive() {return isAlive.get();}
    public boolean isGround() {return isGround.get();}

    public void setX(double val) {x.set(val);}
    public void setY(double val) {y.set(val);}
    public void setHp(double val) {hp.set(val);}
    public void setMaxHp(double val) {maxHp.set(val);}
    public void setAtk(double val) {atk.set(val);}
    public void setAttackInterval(double val) {attackInterval.set(val);}
    public void setDefense(double val) {defense.set(val);}
    public void setResistance(double val) {resistance.set(val);}
    public void setBlockCount(int val) {blockCount.set(val);}
    public void setAttackType(AttackType val) {attackType.set(val);}
    public void setIsAlive(boolean val) {isAlive.set(val);}
    public void setIsGround(boolean val) {isGround.set(val);}

    public DoubleProperty xProperty() { return x; }
    public DoubleProperty yProperty() { return y; }
    public DoubleProperty hpProperty() { return hp; }
    public DoubleProperty maxHpProperty() { return maxHp; }
    public DoubleProperty atkProperty() { return atk; }
    public DoubleProperty attackIntervalProperty() { return attackInterval; }
    public DoubleProperty defenseProperty() { return defense; }
    public DoubleProperty resistanceProperty() { return resistance; }
    public IntegerProperty blockCountProperty() { return blockCount; }
    public ObjectProperty<AttackType> attackTypeProperty() { return attackType; }
    public BooleanProperty isAliveProperty() { return isAlive; }
    public BooleanProperty isGroundProperty() { return isGround; }
}