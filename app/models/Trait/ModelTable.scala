package models.Trait

trait ModelTable[PK] {

  def id : slick.lifted.Rep[PK]
}
