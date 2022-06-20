package models.Trait

trait IdentifyableTable[PK] {

  def id : slick.lifted.Rep[PK]
}
