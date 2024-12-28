(ns harism.three
  (:require
   ["three" 
    :refer [CanvasTexture
            Color
            MeshNormalMaterial
            Mesh
            NearestFilter
            PerspectiveCamera
            RepeatWrapping
            Scene
            TorusKnotGeometry
            Uniform
            WebGLRenderer]]
   ["postprocessing" 
    :refer [Effect
            EffectComposer
            EffectPass
            RenderPass]]
   ["three/addons/controls/OrbitControls.js" 
    :refer [OrbitControls]]))

(defn scene []
  (Scene.))

(defn camera [fov aspect near far]
  (PerspectiveCamera. fov aspect near far))

(defn torus-knot-geometry
  [{:keys [radius tube tubular-segments radial-segments p q]}]
  (TorusKnotGeometry.
   radius
   tube
   tubular-segments
   radial-segments
   (or p 2)
   (or q 3)))

(defn mesh-material []
  (MeshNormalMaterial.))

(defn mesh [geometry material]
  (Mesh. geometry material))

(defn webgl-renderer [canvas-id]
  (WebGLRenderer. #js {:canvas (js/document.getElementById canvas-id)
                       :antialias true
                       :alpha true}))

(defn renderer [width height canvas-id]
  (doto (webgl-renderer canvas-id)
    (.setSize width height)
    (.setPixelRatio (.-devicePixelRatio js/window))))

(defn orbit-controls [camera elem]
  (OrbitControls. camera elem))

(defn render-pass [scene camera]
  (RenderPass. scene camera))

(defn effect-pass [camera effect]
  (EffectPass. camera effect))

(defn effect-composer [renderer]
  (EffectComposer. renderer))

(defn composer [renderer scene camera effect]
  (doto (effect-composer renderer)
    (.addPass (render-pass scene camera))
    (.addPass (effect-pass camera effect))))

(defn canvas-texture 
  [canvas &
   {:keys [mapping wrap-s wrap-t mag-filter min-filter]
    :or {mapping nil
         wrap-s RepeatWrapping
         wrap-t RepeatWrapping
         mag-filter NearestFilter
         min-filter NearestFilter}}]
  (CanvasTexture. canvas mapping wrap-s wrap-t mag-filter min-filter))

(defn uniform
  [x]
  (Uniform. x))

(defn animate! [orbit renderer scene camera]
  (.update orbit)
  (.render renderer scene camera)
  (js/requestAnimationFrame
   #(animate! orbit renderer scene camera))) 

(def ascii-fragment-shader
  "uniform sampler2D characters;
   uniform float characters_count;
   uniform float cell_size;
   uniform vec3 color;

   const vec2 SIZE = vec2(16.);

   vec3 grayscale(vec3 color, float strength) {
       return mix(color, 
                  vec3(dot(color, vec3(0.66, 0.33, 0.66))), 
                  strength);
   }

   void mainImage(const in vec4 inputColor, const in vec2 uv, out vec4 outputColor) {
       vec2 cell = resolution / cell_size;
       vec2 grid = 1.0 / cell;
       vec2 pixelizedUV = grid * (0.5 + floor(uv / grid));
       vec4 pixelized = texture2D(inputBuffer, pixelizedUV);
       float grayscaled = grayscale(pixelized.rgb, 1.0).g;
       float characterIndex = floor((characters_count - 1.0) * grayscaled);
       vec2 characterPosition = vec2(mod(characterIndex, SIZE.x), floor(characterIndex / SIZE.y));
       vec2 offset = vec2(characterPosition.x, -characterPosition.y) / SIZE;
       vec2 charUV = mod(uv * (cell / SIZE), 1.0 / SIZE) - vec2(0., 1.0 / SIZE) + offset;
       
       vec4 ascii = texture2D(characters, charUV);
       ascii.rgb = color * ascii.r;
       ascii.a = pixelized.a;

       if (ascii.r == 0.0 && ascii.g == 0.0 && ascii.b == 0.0) {
           ascii = vec4(0.0, 0.0, 0.0, 0.0);
       }

       outputColor = ascii;
   }")

(defn ascii-canvas-texture
  [{:keys [characters font font-size canvas-size row-size]
    :or   {canvas-size 1024 row-size 16}}]
  (let [canvas (.createElement js/document "canvas")
        cell-size (/ canvas-size row-size) 
        texture (canvas-texture canvas)
        ctx (.getContext canvas "2d")]

    (set! (.-width canvas) canvas-size)
    (set! (.-height canvas) canvas-size) 
    
    (set! (.-font ctx) (str font-size "px " font))
    (set! (.-textAlign ctx) "center")
    (set! (.-textBaseline ctx) "middle")
    (set! (.-fillStyle ctx) "#fff")
    (.clearRect ctx 0 0 canvas-size canvas-size)

    (doseq [i (range (count characters))]
      (let [char (nth characters i)
            x (* (mod i row-size) cell-size)
            y (* (js/Math.floor (/ i row-size)) cell-size)]
        (.fillText ctx char 
                   (+ x (/ cell-size 2)) 
                   (+ y (/ cell-size 2)))))
    
    (set! (.-needsUpdate texture) true)
    texture))

(defn ascii-effect
  [{:keys [font characters font-size cell-size color]
    :or   {font "monospace"
           characters "|\\/*o"
           font-size 72
           cell-size 12
           color "#ffffff"}}]
  (let [uniforms (js/Map.)
        texture (ascii-canvas-texture 
                 {:characters characters 
                  :font font 
                  :font-size font-size})]
   
    (doto uniforms
     (.set "characters" (uniform texture)) 
     (.set "characters_count" (uniform (count characters)))
     (.set "cell_size" (uniform cell-size)) 
     (.set "color" (uniform (Color. color)))) 

    (Effect.
     "AsciiEffect"
     ascii-fragment-shader
     (clj->js {:uniforms uniforms}))))