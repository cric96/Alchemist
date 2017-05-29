package it.unibo.alchemist.boundary.gui.effects;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.reflections.Reflections;

import javafx.scene.control.ChoiceDialog;

/**
 * Class that lets the user choose the effect from all it can find.
 */
public class EffectBuilderFX {
    private static final Reflections REFLECTIONS = new Reflections("it.unibo.alchemist");
    private static final Set<Class<? extends Effect>> EFFECTS = REFLECTIONS.getSubTypesOf(Effect.class);

    private final List<Class<? extends Effect>> effects;
    private final ChoiceDialog<Class<? extends Effect>> dialog;

    /**
     * Default constructor.
     */
    public EffectBuilderFX() {
        effects = new ArrayList<>(EFFECTS);

        dialog = new ChoiceDialog<>(effects.get(0), effects);
        dialog.setTitle("Add an effect");
        dialog.setHeaderText("Choose an effect to load");
        dialog.setContentText(null);
    }

    /**
     * Asks the user to chose an effect and returns the related Class.
     * 
     * @return the class of the effect
     */
    public Optional<Class<? extends Effect>> getResult() {
        return dialog.showAndWait();
    }

    /**
     * Instantiates the desired effect.
     * 
     * @param clazz
     *            the class of the effect
     * @return the effect instantiated
     */
    public Effect instantiateEffect(final Class<? extends Effect> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException("Could not instantiate the effect", e);
        }
    }

    /**
     * Asks the user to chose an effect and returns a new instance of the
     * desired class.
     * <p>
     * Call this method is the same as calling
     * {@link EffectBuilderFX#getResult()} and {@link #chooseAndLoad()}.
     * 
     * @return the effect chosen, or null if no effect was chosen
     */
    public Effect chooseAndLoad() {
        final Optional<Class<? extends Effect>> result = getResult();
        if (result.isPresent()) {
            return this.instantiateEffect(result.get());
        } else {
            return null;
        }
    }
}
