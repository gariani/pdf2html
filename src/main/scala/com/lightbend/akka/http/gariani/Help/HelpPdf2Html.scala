package com.lightbend.akka.http.gariani.Help

/**
 * Created by daniel on 11/10/17.
 */

import io.circe.parser._

object HelpPdf2Html {

  lazy val help: String =
    s"""{
			 "firstPage": "first page to convert (default: 1)",
       "lastPage": "last page to convert (default: 2147483647)",
       "url": "http://path/to/file"
       }
		 """
}

/*val help =
    s"""{
    "--first-page": "first page to convert (default: 1)",
    "--last-page": "last page to convert (default: 2147483647)",
    "--zoom": "zoom ratio",
    "--fit-width": "fit width to <fp> pixels",
    "--fit-height": "fit height to <fp> pixels",
    "--use-cropbox": "use CropBox instead of MediaBox (default: 1)",
    "--hdpi": "horizontal resolution for graphics in DPI (default: 144)",
    "--vdpi": "vertical resolution for graphics in DPI (default: 144)",
    "--embed": "specify which elements should be embedded into output",
    "--embed-css": "embed CSS files into output (default: 1)",
    "--embed-font": "embed font files into output (default: 1)",
    "--embed-image": "embed image files into output (default: 1)",
    "--embed-javascript": "embed JavaScript files into output (default: 1)",
    "--embed-outline": "embed outlines into output (default: 1)",
    "--split-pages": "split pages into separate files (default: 0)",
    "--dest-dir": "specify destination directory (default: \"\\.\")",
    "--css-filename": "filename of the generated css file (default: \"\")",
    "--page-filename": "filename template for split pages  (default: \"\")",
    "--outline-filename": "filename of the generated outline file (default: \"\")",
    "--process-nontext": "render graphics in addition to text (default: 1)",
    "--process-outline": "show outline in HTML (default: 1)",
    "--process-annotation": "show annotation in HTML (default: 0)",
    "--process-form": "include text fields and radio buttons (default: 0)",
    "--printing": "enable printing support (default: 1)",
    "--fallback": "output in fallback mode (default: 0)",
    "--tmp-file-size-limit": "Maximum size (in KB) used by temporary files, -1 for no limit\\. (default: -1)",
    "--embed-external-font": "embed local match for external fonts (default: 1)",
    "--font-format": "suffix for embedded font files (ttf,otf,woff,svg) (default: \"woff\")",
    "--decompose-ligature": "decompose ligatures, such as ﬁ -> fi (default: 0)",
    "--auto-hint": "use fontforge autohint on fonts without hints (default: 0)",
    "--external-hint-tool": "external tool for hinting fonts (overrides --auto-hint) (default: \"\")",
    "--stretch-narrow-glyph": "stretch narrow glyphs instead of padding them (default: 0)",
    "--squeeze-wide-glyph": "shrink wide glyphs instead of truncating them (default: 1)",
    "--override-fstype": "clear the fstype bits in TTF/OTF fonts (default: 0)",
    "--process-type3": "convert Type 3 fonts for web (experimental) (default: 0)",
    "--heps": "horizontal threshold for merging text, in pixels (default: 1)",
    "--veps": "vertical threshold for merging text, in pixels (default: 1)",
    "--space-threshold": "word break threshold (threshold * em) (default: 0\\.125)",
    "--font-size-multiplier": "a value greater than 1 increases the rendering accuracy (default: 4)",
    "--space-as-offset": "treat space characters as offsets (default: 0)",
    "--tounicode": "how to handle ToUnicode CMaps (0=auto, 1=force, -1=ignore) (default: 0)",
    "--optimize-text": "try to reduce the number of HTML elements used for text (default: 0)",
    "--correct-text-visibility": "try to detect texts covered by other graphics and properly arrange them (default: 0)",
    "--bg-format": "specify background image format (default: \"png\")",
    "--svg-node-count-limit": "if node count in a svg background image exceeds this limit, fall back this page to bitmap background; negative value means no limit. (default: -1)",
    "--svg-embed-bitmap": "embed bitmaps in svg background; 0: dump bitmaps to external files if possible\\. (default: 1)",
    "--owner-password": "owner password (for encrypted files)",
    "--user-password": "user password (for encrypted files)",
    "--no-drm": "override document DRM settings (default: 0)",
    "--clean-tmp": "remove temporary files after conversion (default: 1)",
    "--tmp-dir": "specify the location of temporary directory\\. (default: \"/tmp\")",
    "--data-dir": "specify data directory (default: \"/usr/local/share/pdf2htmlEX\")",
    "--poppler-data-dir": "specify poppler data directory (default: \"\")",
    "--proof": "texts are drawn on both text layer and background for proof\\. (default: 0)"
  }"""*/

