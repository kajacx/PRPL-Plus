package com.prplplus.shipconstruct.parts;

public class RotatablePart {
    private ShipPart[] parts = new ShipPart[8];
    private SquareIsomorph activeRotation = SquareIsomorph.Identity;

    public RotatablePart(ShipPart part) {
        parts[SquareIsomorph.Identity.ordinal()] = part;
        parts[SquareIsomorph.RotateCCW.ordinal()] = ShipPartRotator.rotateCCW(part);
        parts[SquareIsomorph.RotateCW.ordinal()] = ShipPartRotator.rotateCW(part);
        parts[SquareIsomorph.FlipHorizontaly.ordinal()] = ShipPartRotator.flipHorizontaly(part);
        parts[SquareIsomorph.FlipVerticaly.ordinal()] = ShipPartRotator.flipVerticaly(part);

        SquareIsomorph rotate180 = SquareIsomorph.FlipVerticaly.andThen(SquareIsomorph.FlipHorizontaly);
        parts[rotate180.ordinal()] = ShipPartRotator.flipHorizontaly(parts[SquareIsomorph.FlipVerticaly.ordinal()]);

        SquareIsomorph flipMajor = SquareIsomorph.FlipVerticaly.andThen(SquareIsomorph.RotateCCW);
        parts[flipMajor.ordinal()] = ShipPartRotator.rotateCCW(parts[SquareIsomorph.FlipVerticaly.ordinal()]);

        SquareIsomorph flipMinor = SquareIsomorph.FlipHorizontaly.andThen(SquareIsomorph.RotateCCW);
        parts[flipMinor.ordinal()] = ShipPartRotator.rotateCCW(parts[SquareIsomorph.FlipHorizontaly.ordinal()]);

        for (int i = 1; i < parts.length; i++) {
            parts[i].rotator = this;
        }
    }

    public ShipPart applyRotation(SquareIsomorph rotation) {
        activeRotation = activeRotation.andThen(rotation);
        return parts[activeRotation.ordinal()];
    }

    public ShipPart withRelativeRotation(SquareIsomorph rotation) {
        return parts[activeRotation.andThen(rotation).ordinal()];
    }

    public ShipPart getOriginalPart() {
        return parts[SquareIsomorph.Identity.ordinal()];
    }

    public void reset() {
        activeRotation = SquareIsomorph.Identity;
    }
}
