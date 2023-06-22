# Table of contents
-  [`rain.biff`](#rain.biff) 
    -  [`use-chime`](#rain.biff/use-chime)
    -  [`use-jetty`](#rain.biff/use-jetty)
    -  [`use-shadow-cljs`](#rain.biff/use-shadow-cljs)
-  [`rain.core`](#rain.core) 
    -  [`export-pages`](#rain.core/export-pages)
    -  [`meta-tags`](#rain.core/meta-tags)
    -  [`script-tags`](#rain.core/script-tags)
    -  [`site-routes`](#rain.core/site-routes)
    -  [`static-pages`](#rain.core/static-pages)
    -  [`stylesheet-tags`](#rain.core/stylesheet-tags)
    -  [`wrap-page`](#rain.core/wrap-page)
-  [`rain.re-frame`](#rain.re-frame) 
    -  [`*app-db*`](#rain.re-frame/*app-db*)
    -  [`atom`](#rain.re-frame/atom)
    -  [`current-page`](#rain.re-frame/current-page)
    -  [`dispatch`](#rain.re-frame/dispatch)
    -  [`dispatch-sync`](#rain.re-frame/dispatch-sync)
    -  [`dispatcher`](#rain.re-frame/dispatcher)
    -  [`event`](#rain.re-frame/event)
    -  [`reg-event-db`](#rain.re-frame/reg-event-db)
    -  [`reg-event-fx`](#rain.re-frame/reg-event-fx)
    -  [`reg-sub`](#rain.re-frame/reg-sub)
    -  [`set-page`](#rain.re-frame/set-page)
    -  [`subscribe`](#rain.re-frame/subscribe)
    -  [`wrap-rf`](#rain.re-frame/wrap-rf)

-----
# <a name="rain.biff">rain.biff</a>






## <a name="rain.biff/use-chime">`use-chime`</a><a name="rain.biff/use-chime"></a>
``` clojure

(use-chime {:keys [biff/features biff/plugins biff.chime/tasks], :as ctx})
```
<p><sub><a href="/blob/main/src/rain/biff.clj#L30-L38">Source</a></sub></p>

## <a name="rain.biff/use-jetty">`use-jetty`</a><a name="rain.biff/use-jetty"></a>
``` clojure

(use-jetty {:biff/keys [host port handler], :or {host "localhost", port 8080}, :as ctx})
```
<p><sub><a href="/blob/main/src/rain/biff.clj#L8-L18">Source</a></sub></p>

## <a name="rain.biff/use-shadow-cljs">`use-shadow-cljs`</a><a name="rain.biff/use-shadow-cljs"></a>
``` clojure

(use-shadow-cljs {:keys [shadow-cljs/mode], :as ctx})
```
<p><sub><a href="/blob/main/src/rain/biff.clj#L20-L28">Source</a></sub></p>

-----
# <a name="rain.core">rain.core</a>






## <a name="rain.core/export-pages">`export-pages`</a><a name="rain.core/export-pages"></a>
``` clojure

(export-pages pages dir)
```
<p><sub><a href="/blob/main/src/rain/core.clj#L71-L77">Source</a></sub></p>

## <a name="rain.core/meta-tags">`meta-tags`</a><a name="rain.core/meta-tags"></a>
``` clojure

(meta-tags ctx)
```
<p><sub><a href="/blob/main/src/rain/core.clj#L39-L40">Source</a></sub></p>

## <a name="rain.core/script-tags">`script-tags`</a><a name="rain.core/script-tags"></a>
``` clojure

(script-tags ctx)
```
<p><sub><a href="/blob/main/src/rain/core.clj#L52-L55">Source</a></sub></p>

## <a name="rain.core/site-routes">`site-routes`</a><a name="rain.core/site-routes"></a>
``` clojure

(site-routes routes)
```
<p><sub><a href="/blob/main/src/rain/core.clj#L79-L82">Source</a></sub></p>

## <a name="rain.core/static-pages">`static-pages`</a><a name="rain.core/static-pages"></a>
``` clojure

(static-pages routes & {:as ctx})
```
<p><sub><a href="/blob/main/src/rain/core.clj#L96-L109">Source</a></sub></p>

## <a name="rain.core/stylesheet-tags">`stylesheet-tags`</a><a name="rain.core/stylesheet-tags"></a>
``` clojure

(stylesheet-tags ctx)
```
<p><sub><a href="/blob/main/src/rain/core.clj#L49-L50">Source</a></sub></p>

## <a name="rain.core/wrap-page">`wrap-page`</a><a name="rain.core/wrap-page"></a>
``` clojure

(wrap-page handler)
```
<p><sub><a href="/blob/main/src/rain/core.clj#L57-L69">Source</a></sub></p>

-----
# <a name="rain.re-frame">rain.re-frame</a>






## <a name="rain.re-frame/*app-db*">`*app-db*`</a><a name="rain.re-frame/*app-db*"></a>



<p><sub><a href="/blob/main/src/rain/re_frame.cljc#L10-L12">Source</a></sub></p>

## <a name="rain.re-frame/atom">`atom`</a><a name="rain.re-frame/atom"></a>
``` clojure

(atom init-val)
```
<p><sub><a href="/blob/main/src/rain/re_frame.cljc#L85-L87">Source</a></sub></p>

## <a name="rain.re-frame/current-page">`current-page`</a><a name="rain.re-frame/current-page"></a>
``` clojure

(current-page _)
```
<p><sub><a href="/blob/main/src/rain/re_frame.cljc#L42-L46">Source</a></sub></p>

## <a name="rain.re-frame/dispatch">`dispatch`</a><a name="rain.re-frame/dispatch"></a>
``` clojure

(dispatch event)
```
<p><sub><a href="/blob/main/src/rain/re_frame.cljc#L79-L80">Source</a></sub></p>

## <a name="rain.re-frame/dispatch-sync">`dispatch-sync`</a><a name="rain.re-frame/dispatch-sync"></a>
``` clojure

(dispatch-sync event)
```
<p><sub><a href="/blob/main/src/rain/re_frame.cljc#L82-L83">Source</a></sub></p>

## <a name="rain.re-frame/dispatcher">`dispatcher`</a><a name="rain.re-frame/dispatcher"></a>
``` clojure

(dispatcher event)
```
<p><sub><a href="/blob/main/src/rain/re_frame.cljc#L73-L77">Source</a></sub></p>

## <a name="rain.re-frame/event">`event`</a><a name="rain.re-frame/event"></a>
``` clojure

(event & body)
```
Function.
<p><sub><a href="/blob/main/src/rain/re_frame.cljc#L94-L96">Source</a></sub></p>

## <a name="rain.re-frame/reg-event-db">`reg-event-db`</a><a name="rain.re-frame/reg-event-db"></a>
``` clojure

(reg-event-db id handler)
(reg-event-db id interceptors handler)
```
<p><sub><a href="/blob/main/src/rain/re_frame.cljc#L61-L65">Source</a></sub></p>

## <a name="rain.re-frame/reg-event-fx">`reg-event-fx`</a><a name="rain.re-frame/reg-event-fx"></a>
``` clojure

(reg-event-fx id handler)
(reg-event-fx id interceptors handler)
```
<p><sub><a href="/blob/main/src/rain/re_frame.cljc#L67-L71">Source</a></sub></p>

## <a name="rain.re-frame/reg-sub">`reg-sub`</a><a name="rain.re-frame/reg-sub"></a>
``` clojure

(reg-sub query-id f)
```
<p><sub><a href="/blob/main/src/rain/re_frame.cljc#L51-L53">Source</a></sub></p>

## <a name="rain.re-frame/set-page">`set-page`</a><a name="rain.re-frame/set-page"></a>
``` clojure

(set-page {:keys [db]} [_ match])
```
<p><sub><a href="/blob/main/src/rain/re_frame.cljc#L23-L30">Source</a></sub></p>

## <a name="rain.re-frame/subscribe">`subscribe`</a><a name="rain.re-frame/subscribe"></a>
``` clojure

(subscribe query)
```
<p><sub><a href="/blob/main/src/rain/re_frame.cljc#L55-L59">Source</a></sub></p>

## <a name="rain.re-frame/wrap-rf">`wrap-rf`</a><a name="rain.re-frame/wrap-rf"></a>
``` clojure

(wrap-rf page-fn)
```
<p><sub><a href="/blob/main/src/rain/re_frame.cljc#L98-L104">Source</a></sub></p>
