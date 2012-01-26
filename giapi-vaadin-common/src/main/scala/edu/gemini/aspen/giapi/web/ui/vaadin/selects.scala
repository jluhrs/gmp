package edu.gemini.aspen.giapi.web.ui.vaadin

import com.vaadin.data.Container
import com.vaadin.ui.Table.ColumnGenerator

object selects {

  /**
   * Traits that implements adding a function as property value change event listener */
  trait ValueChangeFunction extends com.vaadin.data.Property.ValueChangeNotifier {
    def addListener(action: com.vaadin.data.Property.ValueChangeEvent => Unit): Unit = addListener(new PropertyValueChangeListener(action))
  }

  /**
   * Adapter class to call a function upon a value change event */
  private class PropertyValueChangeListener(action: com.vaadin.data.Property.ValueChangeEvent => Unit)
    extends com.vaadin.data.Property.ValueChangeListener {
    override def valueChange(event: com.vaadin.data.Property.ValueChangeEvent) = action(event)
  }

  /**
   * Trait implementing a TableColumnGenerator */
  trait TableColumnGenerator extends com.vaadin.ui.Table {
    def addGeneratedColumn(id: Any, generate: (com.vaadin.ui.Table, AnyRef, AnyRef) => AnyRef): Unit = {
      addGeneratedColumn(id, new ColumnGenerator {
        def generateCell(table: com.vaadin.ui.Table,
                         itemId: AnyRef,
                         columnId: AnyRef): AnyRef = {
          generate(table, itemId, columnId)
        }
      })
    }

    def addGeneratedColumn(id: Any, generate: (AnyRef, AnyRef) => AnyRef): Unit = {
      addGeneratedColumn(id, new ColumnGenerator {
        def generateCell(table: com.vaadin.ui.Table,
                         itemId: AnyRef,
                         columnId: AnyRef): AnyRef = {
          generate(itemId, columnId)
        }
      })
    }
  }

  /**
   * Traits that implements adding a function as an item click listener */
  trait ItemClickListener extends com.vaadin.event.ItemClickEvent.ItemClickNotifier {
    def addItemClickListener(action: com.vaadin.event.ItemClickEvent => Unit) {
      addListener(new com.vaadin.event.ItemClickEvent.ItemClickListener {
        override def itemClick(event: com.vaadin.event.ItemClickEvent) = action(event)
      })
    }
  }

  /**
   * Scala wrapper for the com.vaadin.ui.Table class */
  class Table(caption: String = null,
              width: String = null,
              height: String = null,
              dataSource: Container = null,
              property: com.vaadin.data.Property = null,
              value: Any = null,
              selectable: Boolean = false,
              immediate: Boolean = false,
              sizeFull: Boolean = false,
              columnReorderingAllowed: Boolean = true,
              sortAscending: Boolean = true,
              sortPropertyId: String = null,
              style: String = null,
              cellStyleGenerator: (AnyRef, AnyRef) => String = null)
    extends com.vaadin.ui.Table(caption) with ValueChangeFunction with ItemClickListener with TableColumnGenerator {
    setWidth(width)
    setHeight(height)
    if (sizeFull) setSizeFull()

    if (dataSource != null) setContainerDataSource(dataSource)
    if (property != null) setPropertyDataSource(property)
    if (value != null) setValue(value)

    if (sortPropertyId != null) setSortContainerPropertyId(sortPropertyId)
    if (cellStyleGenerator != null) setCellStyleGenerator(new com.vaadin.ui.Table.CellStyleGenerator() {
      override def getStyle(itemId: AnyRef, propertyId: AnyRef) = cellStyleGenerator(itemId, propertyId)
    })
    setColumnReorderingAllowed(columnReorderingAllowed)
    setSortAscending(sortAscending)
    setSelectable(selectable)
    setImmediate(immediate)
    setStyleName(style)
  }

}