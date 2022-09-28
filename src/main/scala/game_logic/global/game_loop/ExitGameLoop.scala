package game_logic.global.game_loop

// TODO: Add all the game state data as param, to save it before exit (update run)
case class ExitGameLoop[T <: GameLoopParams](state: T) extends BaseGameLoop[T] {
  override def run: BaseGameLoop[T] = { System.exit(0); this }

  override def setState(newState: T): BaseGameLoop[T] =
    this.setState(newState)

  override def getState: T = state
}
