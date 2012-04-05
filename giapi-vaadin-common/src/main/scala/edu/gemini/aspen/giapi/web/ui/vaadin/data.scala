package edu.gemini.aspen.giapi.web.ui.vaadin

package object data {

  /**
   * Companion object for com.vaadin.data.Property */
  object Property {
    def apply[T](value: T): com.vaadin.data.Property = new com.vaadin.data.util.ObjectProperty[T](value)
  }

  /**
   * Companion object for com.vaadin.data.Item */
  object Item {
    def apply(properties: Tuple2[Any, Any]*): com.vaadin.data.Item = {
      val item = new com.vaadin.data.util.PropertysetItem
      properties foreach {p => item.addItemProperty(p._1, Property(p._2))}
      item
    }
  }

  object Container {
    def apply(items: Tuple2[Any, Seq[Tuple2[Any, Any]]]*) = {
      val container = new com.vaadin.data.util.IndexedContainer
      for (item <- items) {
        val containerItem = container.addItem(item._1)
        for (property <- item._2) {
          container.addContainerProperty(property._1, property._2.getClass, null)
          containerItem.getItemProperty(property._1).setValue(property._2)
        }
      }

      container
    }
  }

}