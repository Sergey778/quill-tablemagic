package tablemagic

import tablemagic.ConnectionType.One

import scala.annotation.StaticAnnotation
import scala.meta._

class References[TableType](
                connectionType: ConnectionType = One,
                generatedFieldName: String = "",
                tableFieldName: String = ""
                ) extends StaticAnnotation
