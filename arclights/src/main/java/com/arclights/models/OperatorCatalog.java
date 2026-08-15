package com.arclights.models;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;

import com.arclights.entity.operator.Caster;
import com.arclights.entity.operator.Defender;
import com.arclights.entity.operator.Guard;
import com.arclights.entity.operator.Medic;
import com.arclights.entity.operator.Operator;
import com.arclights.entity.operator.Sniper;
import com.arclights.entity.operator.Specialist;
import com.arclights.entity.operator.Supporter;
import com.arclights.entity.operator.Vanguard;

import javafx.scene.paint.Color;

/**
 * Single source of truth for every recruitable operator: id (matches the
 * PlayerProgress/ShopMenu save key), display info, DP deploy cost, tile
 * placement rule and the factory used to actually instantiate it in battle.
 *
 * Both ShopMenu (recruiting with crystals) and OperatorDeploymentBar
 * (showing deployable cards in battle) key off {@link PlayerProgress#ownsOperator}
 * with these same ids, so an operator recruited in the shop automatically
 * becomes deployable the next time a stage is entered - no separate wiring
 * needed. To add a new operator: create its Operator subclass, add one entry
 * below, and drop its sprites under
 * /sprites/operators/&lt;ClassSimpleName-lowercase&gt;/{start,idle,walk,attack,death}/.
 */
public final class OperatorCatalog {

    public enum Placement { RANGED, MELEE }

    public static final class Definition {
        public final String id;
        public final String displayName;
        public final String type;
        public final int deployCost;
        public final Placement placement;
        public final String portraitPath;
        public final Color cardColor;
        private final BiFunction<Integer, Integer, Operator> factory;

        private Definition(String id, String displayName, String type, int deployCost, Placement placement,
                            String portraitPath, Color cardColor, BiFunction<Integer, Integer, Operator> factory) {
            this.id = id;
            this.displayName = displayName;
            this.type = type;
            this.deployCost = deployCost;
            this.placement = placement;
            this.portraitPath = portraitPath;
            this.cardColor = cardColor;
            this.factory = factory;
        }

        /** Instantiates a real Operator of this type at the given grid coordinates. */
        public Operator create(int gridCol, int gridRow) {
            return factory.apply(gridCol, gridRow);
        }
    }

    public static final String[] STARTER_OPERATOR_IDS = {
        "placeholder_operator_1",
        "placeholder_operator_2"
    };

    private static final Definition[] DEFINITIONS = {
        new Definition(
            "placeholder_operator_1", "Kroos", "SNIPER",
            Sniper.DEPLOY_COST, Placement.RANGED,
            "/com/arclights/char/char_124_kroos_sale#14 #15005.png", Color.web("#4caf50"),
            (col, row) -> new Sniper(col, row)),

        new Definition(
            "placeholder_operator_2", "Beagle", "DEFENDER",
            Defender.DEPLOY_COST, Placement.MELEE,
            "/com/arclights/char/char_122_beagle_boc#1 #15657.png", Color.web("#2196f3"),
            (col, row) -> new Defender(col, row)),

        new Definition(
            "placeholder_operator_3", "Eyjafjalla", "CASTER",
            Caster.DEPLOY_COST, Placement.RANGED,
            "/com/arclights/char/char_180_amgoat_summer#5 #15978.png", Color.web("#9b59b6"),
            (col, row) -> new Caster(col, row)),

        new Definition(
            "placeholder_operator_4", "Hibicus", "MEDIC",
            Medic.DEPLOY_COST, Placement.RANGED,
            "/com/arclights/char/char_120_hibisc_nian#1 #15736.png", Color.web("#2ecc71"),
            (col, row) -> new Medic(col, row)),

        new Definition(
            "placeholder_operator_5", "Blaze", "GUARD",
            Guard.DEPLOY_COST, Placement.MELEE,
            "/com/arclights/char/char_017_huang_witch#5 #14843.png", Color.web("#e67e22"),
            (col, row) -> new Guard(col, row)),

        new Definition(
            "placeholder_operator_6", "Projekt Red", "SPECIALIST",
            Specialist.DEPLOY_COST, Placement.MELEE,
            "/com/arclights/char/char_144_red_summer#6 #16091.png", Color.web("#f1c40f"),
            (col, row) -> new Specialist(col, row)),
        
        new Definition(
            "placeholder_operator_7", "Fang", "VANGUARD",
            Vanguard.DEPLOY_COST, Placement.MELEE,
            "/com/arclights/char/char_1036_fang2_snow#8 #15485.png", Color.web("#3498db"),
            (col, row) -> new Vanguard(col, row)),

        new Definition(
            "placeholder_operator_8", "Skadi", "SUPPORTER",
            Supporter.DEPLOY_COST, Placement.RANGED,
            "/com/arclights/char/char_1012_skadi2_boc#4 #15713.png", Color.web("#1abc9c"),
            (col, row) -> new Supporter(col, row)),
    };

    private static final Map<String, Definition> BY_ID = new LinkedHashMap<>();
    static {
        for (Definition def : DEFINITIONS) {
            BY_ID.put(def.id, def);
        }
    }

    private OperatorCatalog() {
    }

    /** Looks up a definition by its save/shop id, or null if unknown. */
    public static Definition get(String operatorId) {
        return operatorId == null ? null : BY_ID.get(operatorId);
    }

    /** All definitions, in a stable catalog/display order. */
    public static Definition[] all() {
        return DEFINITIONS.clone();
    }
}
