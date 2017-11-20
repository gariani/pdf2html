package com.lightbend.akka.http.gariani.Custom

/**
 * Created by daniel on 03/10/17.
 */

final case class Parameters(
  firstPage: Option[Int] = Some(1),
  lastPage: Option[Int] = Some(Int.MaxValue),
  url: String
)

object Parameter {

  import com.wix.accord._
  import dsl._

	implicit private val parametersValidator = validator[Parameters] { o =>
    (o.firstPage.each as "firstPage" must be >= (1)) or (o.firstPage.each is aNull)
    (o.lastPage.each as "lastPage" must be >= (1)) or (o.lastPage.each as "lastPage" must be <= Int.MaxValue) or (o.lastPage.each is aNull)
  }

  def validParameters(op: Parameters): Either[String, Parameters] = {
    validate(op) match {
      case Success => Right(op)
      case Failure(e) => print(e.mkString); Left(e.toString())
    }
  }
}

/*case class Parameters(firstPage: Option[Int] = Some(1),
                            lastPage: Option[Int],
                            zoom: Option[Int],
                            fitWidth: Option[Int],
                            fitHeight: Option[Int],
                            useCropbox: Option[Boolean] = Some(true),
                            hdpi: Option[Int] = Some(144),
                            vdpi: Option[Int] = Some(144),
                            embed: Option[String],
                            embedCss: Option[Boolean] = Some(false),
                            embedFont: Option[Boolean] = Some(false),
                            embedImage: Option[Boolean] = Some(false),
                            embedJavascript: Option[Boolean] = Some(false),
                            embedOutline: Option[Boolean] = Some(false),
                            splitPages: Option[Boolean] = Some(false),
                            pageFilename: Option[String] = Some(""),
                            outlineFilename: Option[String],
                            processNontext: Option[Boolean] = Some(false),
                            processOutline: Option[Boolean] = Some(false),
                            processAnnotation: Option[Boolean] = Some(true),
                            processForm: Option[Boolean] = Some(true),
                            printing: Option[Boolean] = Some(false),
                            fallback: Option[Boolean] = Some(true),
                            fontFormat: Option[String] = Some("woff"),
                            decomposeLigature: Option[Boolean] = Some(true),
                            autoHint: Option[Boolean] = Some(true),
                            stretchNarrowGlyph: Option[Boolean] = Some(true),
                            squeezeWideGlyph: Option[Boolean] = Some(false),
                            overrideFstype: Option[Boolean] = Some(true),
                            processType3: Option[Boolean] = Some(true),
                            heps: Option[Int] = Some(1),
                            veps: Option[Int] = Some(1),
                            spaceThreshold: Option[Double] = Some(0.125),
                            fontSizeMultiplier: Option[Double] = Some(4.0),
                            spaceAsOffset: Option[Boolean] = Some(true),
                            toUnicode: Option[Byte] = Some(0),
                            optimizeText: Option[Boolean] = Some(true),
                            correctTextVisibility: Option[Boolean] = Some(true),
                            bgFormat: Option[String] = Some("png"),
                            svgNodeCountLimit: Option[Int] = Some(-1),
                            svgEmbedBitmap: Option[Boolean] = Some(false),
                            ownerPassword: Option[String],
                            userPassword: Option[String],
                            noDrm: Option[Boolean] = Some(true),
                            cleanTmp: Option[Boolean] = Some(false),
                            proof: Option[Byte],
                            url: Option[String])*/

/*object Parameters {

  import com.wix.accord._
  import dsl._

  implicit val paramtersValidator = validator[Parameters] { o =>
    (o.firstPage.each must be >= (1)) or (o.firstPage.each is aNull)
    (o.lastPage.each must >=(1)) or (o.lastPage.each is aNull)
    (o.zoom.each must >=(1)) or (o.zoom.each is aNull)
    (o.fitWidth.each must >=(1)) or (o.fitWidth.each is aNull)
    (o.fitHeight.each must >=(1)) or (o.fitHeight.each is aNull)
    (o.hdpi.each must >=(1)) or (o.hdpi.each is aNull)
    (o.vdpi.each must >=(1)) or (o.vdpi.each is aNull)
    (o.embed.each should matchRegex("\\[cCfFiIjJoO]+")) or (o.embed is aNull)
    (o.heps.each must >=(1)) or (o.heps.each is aNull)
    (o.veps.each must >=(1)) or (o.veps.each is aNull)
    (o.spaceThreshold.each must >=(0.0)) or (o.spaceThreshold is aNull)
    (o.fontSizeMultiplier.each must >=(0.0)) or (o.fontSizeMultiplier is aNull)
    (o.toUnicode.each is in(-1, 1)) or (o.toUnicode.each is aNull)
    (o.bgFormat.each is in("png", "jpg", "svg")) or (o.bgFormat.each is aNull)
    (o.proof.each is aNull) or (o.proof.each is in(0, 1, 2))
    (o.url.each is notNull)
  }

  def valid(op: Parameters) = {
    val v = validate(op)
    v
  }

}*/

