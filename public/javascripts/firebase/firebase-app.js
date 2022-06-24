!function(t,e){"object"==typeof exports&&"undefined"!=typeof module?module.exports=e():"function"==typeof define&&define.amd?define(e):(t=t||self).firebase=e()}(this,function(){"use strict";!function(t){if(!t.fetch){var e="URLSearchParams"in t,r="Symbol"in t&&"iterator"in Symbol,s="FileReader"in t&&"Blob"in t&&function(){try{return new Blob,!0}catch(t){return!1}}(),n="FormData"in t,i="ArrayBuffer"in t;if(i)var o=["[object Int8Array]","[object Uint8Array]","[object Uint8ClampedArray]","[object Int16Array]","[object Uint16Array]","[object Int32Array]","[object Uint32Array]","[object Float32Array]","[object Float64Array]"],a=function(t){return t&&DataView.prototype.isPrototypeOf(t)},u=ArrayBuffer.isView||function(t){return t&&-1<o.indexOf(Object.prototype.toString.call(t))};d.prototype.append=function(t,e){t=h(t),e=l(e);var r=this.map[t];this.map[t]=r?r+","+e:e},d.prototype.delete=function(t){delete this.map[h(t)]},d.prototype.get=function(t){return t=h(t),this.has(t)?this.map[t]:null},d.prototype.has=function(t){return this.map.hasOwnProperty(h(t))},d.prototype.set=function(t,e){this.map[h(t)]=l(e)},d.prototype.forEach=function(t,e){for(var r in this.map)this.map.hasOwnProperty(r)&&t.call(e,this.map[r],r,this)},d.prototype.keys=function(){var r=[];return this.forEach(function(t,e){r.push(e)}),p(r)},d.prototype.values=function(){var e=[];return this.forEach(function(t){e.push(t)}),p(e)},d.prototype.entries=function(){var r=[];return this.forEach(function(t,e){r.push([e,t])}),p(r)},r&&(d.prototype[Symbol.iterator]=d.prototype.entries);var c=["DELETE","GET","HEAD","OPTIONS","POST","PUT"];g.prototype.clone=function(){return new g(this,{body:this._bodyInit})},_.call(g.prototype),_.call(O.prototype),O.prototype.clone=function(){return new O(this._bodyInit,{status:this.status,statusText:this.statusText,headers:new d(this.headers),url:this.url})},O.error=function(){var t=new O(null,{status:0,statusText:""});return t.type="error",t};var f=[301,302,303,307,308];O.redirect=function(t,e){if(-1===f.indexOf(e))throw new RangeError("Invalid status code");return new O(null,{status:e,headers:{location:t}})},t.Headers=d,t.Request=g,t.Response=O,t.fetch=function(r,i){return new Promise(function(n,t){var e=new g(r,i),o=new XMLHttpRequest;o.onload=function(){var t,i,e={status:o.status,statusText:o.statusText,headers:(t=o.getAllResponseHeaders()||"",i=new d,t.replace(/\r?\n[\t ]+/g," ").split(/\r?\n/).forEach(function(t){var e=t.split(":"),r=e.shift().trim();if(r){var n=e.join(":").trim();i.append(r,n)}}),i)};e.url="responseURL"in o?o.responseURL:e.headers.get("X-Request-URL");var r="response"in o?o.response:o.responseText;n(new O(r,e))},o.onerror=function(){t(new TypeError("Network request failed"))},o.ontimeout=function(){t(new TypeError("Network request failed"))},o.open(e.method,e.url,!0),"include"===e.credentials?o.withCredentials=!0:"omit"===e.credentials&&(o.withCredentials=!1),"responseType"in o&&s&&(o.responseType="blob"),e.headers.forEach(function(t,e){o.setRequestHeader(e,t)}),o.send(void 0===e._bodyInit?null:e._bodyInit)})},t.fetch.polyfill=!0}function h(t){if("string"!=typeof t&&(t=String(t)),/[^a-z0-9\-#$%&'*+.\^_`|~]/i.test(t))throw new TypeError("Invalid character in header field name");return t.toLowerCase()}function l(t){return"string"!=typeof t&&(t=String(t)),t}function p(e){var t={next:function(){var t=e.shift();return{done:void 0===t,value:t}}};return r&&(t[Symbol.iterator]=function(){return t}),t}function d(e){this.map={},e instanceof d?e.forEach(function(t,e){this.append(e,t)},this):Array.isArray(e)?e.forEach(function(t){this.append(t[0],t[1])},this):e&&Object.getOwnPropertyNames(e).forEach(function(t){this.append(t,e[t])},this)}function y(t){if(t.bodyUsed)return Promise.reject(new TypeError("Already read"));t.bodyUsed=!0}function v(r){return new Promise(function(t,e){r.onload=function(){t(r.result)},r.onerror=function(){e(r.error)}})}function b(t){var e=new FileReader,r=v(e);return e.readAsArrayBuffer(t),r}function m(t){if(t.slice)return t.slice(0);var e=new Uint8Array(t.byteLength);return e.set(new Uint8Array(t)),e.buffer}function _(){return this.bodyUsed=!1,this._initBody=function(t){if(this._bodyInit=t)if("string"==typeof t)this._bodyText=t;else if(s&&Blob.prototype.isPrototypeOf(t))this._bodyBlob=t;else if(n&&FormData.prototype.isPrototypeOf(t))this._bodyFormData=t;else if(e&&URLSearchParams.prototype.isPrototypeOf(t))this._bodyText=t.toString();else if(i&&s&&a(t))this._bodyArrayBuffer=m(t.buffer),this._bodyInit=new Blob([this._bodyArrayBuffer]);else{if(!i||!ArrayBuffer.prototype.isPrototypeOf(t)&&!u(t))throw new Error("unsupported BodyInit type");this._bodyArrayBuffer=m(t)}else this._bodyText="";this.headers.get("content-type")||("string"==typeof t?this.headers.set("content-type","text/plain;charset=UTF-8"):this._bodyBlob&&this._bodyBlob.type?this.headers.set("content-type",this._bodyBlob.type):e&&URLSearchParams.prototype.isPrototypeOf(t)&&this.headers.set("content-type","application/x-www-form-urlencoded;charset=UTF-8"))},s&&(this.blob=function(){var t=y(this);if(t)return t;if(this._bodyBlob)return Promise.resolve(this._bodyBlob);if(this._bodyArrayBuffer)return Promise.resolve(new Blob([this._bodyArrayBuffer]));if(this._bodyFormData)throw new Error("could not read FormData body as blob");return Promise.resolve(new Blob([this._bodyText]))},this.arrayBuffer=function(){return this._bodyArrayBuffer?y(this)||Promise.resolve(this._bodyArrayBuffer):this.blob().then(b)}),this.text=function(){var t,e,r,n=y(this);if(n)return n;if(this._bodyBlob)return t=this._bodyBlob,e=new FileReader,r=v(e),e.readAsText(t),r;if(this._bodyArrayBuffer)return Promise.resolve(function(t){for(var e=new Uint8Array(t),r=new Array(e.length),n=0;n<e.length;n++)r[n]=String.fromCharCode(e[n]);return r.join("")}(this._bodyArrayBuffer));if(this._bodyFormData)throw new Error("could not read FormData body as text");return Promise.resolve(this._bodyText)},n&&(this.formData=function(){return this.text().then(w)}),this.json=function(){return this.text().then(JSON.parse)},this}function g(t,e){var r,n,i=(e=e||{}).body;if(t instanceof g){if(t.bodyUsed)throw new TypeError("Already read");this.url=t.url,this.credentials=t.credentials,e.headers||(this.headers=new d(t.headers)),this.method=t.method,this.mode=t.mode,i||null==t._bodyInit||(i=t._bodyInit,t.bodyUsed=!0)}else this.url=String(t);if(this.credentials=e.credentials||this.credentials||"omit",!e.headers&&this.headers||(this.headers=new d(e.headers)),this.method=(r=e.method||this.method||"GET",n=r.toUpperCase(),-1<c.indexOf(n)?n:r),this.mode=e.mode||this.mode||null,this.referrer=null,("GET"===this.method||"HEAD"===this.method)&&i)throw new TypeError("Body not allowed for GET or HEAD requests");this._initBody(i)}function w(t){var i=new FormData;return t.trim().split("&").forEach(function(t){if(t){var e=t.split("="),r=e.shift().replace(/\+/g," "),n=e.join("=").replace(/\+/g," ");i.append(decodeURIComponent(r),decodeURIComponent(n))}}),i}function O(t,e){e||(e={}),this.type="default",this.status=void 0===e.status?200:e.status,this.ok=200<=this.status&&this.status<300,this.statusText="statusText"in e?e.statusText:"OK",this.headers=new d(e.headers),this.url=e.url||"",this._initBody(t)}}("undefined"!=typeof self?self:void 0);var t="undefined"!=typeof window?window:"undefined"!=typeof global?global:"undefined"!=typeof self?self:{};function e(t,e){return t(e={exports:{}},e.exports),e.exports}var r=setTimeout;function n(){}function o(t){if(!(this instanceof o))throw new TypeError("Promises must be constructed via new");if("function"!=typeof t)throw new TypeError("not a function");this._state=0,this._handled=!1,this._value=void 0,this._deferreds=[],f(t,this)}function i(r,n){for(;3===r._state;)r=r._value;0!==r._state?(r._handled=!0,o._immediateFn(function(){var t=1===r._state?n.onFulfilled:n.onRejected;if(null!==t){var e;try{e=t(r._value)}catch(t){return void a(n.promise,t)}s(n.promise,e)}else(1===r._state?s:a)(n.promise,r._value)})):r._deferreds.push(n)}function s(e,t){try{if(t===e)throw new TypeError("A promise cannot be resolved with itself.");if(t&&("object"==typeof t||"function"==typeof t)){var r=t.then;if(t instanceof o)return e._state=3,e._value=t,void u(e);if("function"==typeof r)return void f((n=r,i=t,function(){n.apply(i,arguments)}),e)}e._state=1,e._value=t,u(e)}catch(t){a(e,t)}var n,i}function a(t,e){t._state=2,t._value=e,u(t)}function u(t){2===t._state&&0===t._deferreds.length&&o._immediateFn(function(){t._handled||o._unhandledRejectionFn(t._value)});for(var e=0,r=t._deferreds.length;e<r;e++)i(t,t._deferreds[e]);t._deferreds=null}function c(t,e,r){this.onFulfilled="function"==typeof t?t:null,this.onRejected="function"==typeof e?e:null,this.promise=r}function f(t,e){var r=!1;try{t(function(t){r||(r=!0,s(e,t))},function(t){r||(r=!0,a(e,t))})}catch(t){if(r)return;r=!0,a(e,t)}}o.prototype.catch=function(t){return this.then(null,t)},o.prototype.then=function(t,e){var r=new this.constructor(n);return i(this,new c(t,e,r)),r},o.prototype.finally=function(e){var r=this.constructor;return this.then(function(t){return r.resolve(e()).then(function(){return t})},function(t){return r.resolve(e()).then(function(){return r.reject(t)})})},o.all=function(e){return new o(function(n,i){if(!e||void 0===e.length)throw new TypeError("Promise.all accepts an array");var o=Array.prototype.slice.call(e);if(0===o.length)return n([]);var s=o.length;function a(e,t){try{if(t&&("object"==typeof t||"function"==typeof t)){var r=t.then;if("function"==typeof r)return void r.call(t,function(t){a(e,t)},i)}o[e]=t,0==--s&&n(o)}catch(t){i(t)}}for(var t=0;t<o.length;t++)a(t,o[t])})},o.resolve=function(e){return e&&"object"==typeof e&&e.constructor===o?e:new o(function(t){t(e)})},o.reject=function(r){return new o(function(t,e){e(r)})},o.race=function(i){return new o(function(t,e){for(var r=0,n=i.length;r<n;r++)i[r].then(t,e)})},o._immediateFn="function"==typeof setImmediate&&function(t){setImmediate(t)}||function(t){r(t,0)},o._unhandledRejectionFn=function(t){"undefined"!=typeof console&&console&&console.warn("Possible Unhandled Promise Rejection:",t)};var h=function(){if("undefined"!=typeof self)return self;if("undefined"!=typeof window)return window;if(void 0!==t)return t;throw new Error("unable to locate global object")}();h.Promise||(h.Promise=o);var y=e(function(t){var e=t.exports="undefined"!=typeof window&&window.Math==Math?window:"undefined"!=typeof self&&self.Math==Math?self:Function("return this")();"number"==typeof __g&&(__g=e)}),v=e(function(t){var e=t.exports={version:"2.5.5"};"number"==typeof __e&&(__e=e)}),l=(v.version,function(t){return"object"==typeof t?null!==t:"function"==typeof t}),p=function(t){if(!l(t))throw TypeError(t+" is not an object!");return t},d=function(t){try{return!!t()}catch(t){return!0}},b=!d(function(){return 7!=Object.defineProperty({},"a",{get:function(){return 7}}).a}),m=y.document,_=l(m)&&l(m.createElement),g=function(t){return _?m.createElement(t):{}},w=!b&&!d(function(){return 7!=Object.defineProperty(g("div"),"a",{get:function(){return 7}}).a}),O=function(t,e){if(!l(t))return t;var r,n;if(e&&"function"==typeof(r=t.toString)&&!l(n=r.call(t)))return n;if("function"==typeof(r=t.valueOf)&&!l(n=r.call(t)))return n;if(!e&&"function"==typeof(r=t.toString)&&!l(n=r.call(t)))return n;throw TypeError("Can't convert object to primitive value")},S=Object.defineProperty,E={f:b?Object.defineProperty:function(t,e,r){if(p(t),e=O(e,!0),p(r),w)try{return S(t,e,r)}catch(t){}if("get"in r||"set"in r)throw TypeError("Accessors not supported!");return"value"in r&&(t[e]=r.value),t}},A=function(t,e){return{enumerable:!(1&t),configurable:!(2&t),writable:!(4&t),value:e}},j=b?function(t,e,r){return E.f(t,e,A(1,r))}:function(t,e,r){return t[e]=r,t},P={}.hasOwnProperty,T=function(t,e){return P.call(t,e)},k=0,x=Math.random(),F=function(t){return"Symbol(".concat(void 0===t?"":t,")_",(++k+x).toString(36))},N=e(function(t){var o=F("src"),e="toString",r=Function[e],s=(""+r).split(e);v.inspectSource=function(t){return r.call(t)},(t.exports=function(t,e,r,n){var i="function"==typeof r;i&&(T(r,"name")||j(r,"name",e)),t[e]!==r&&(i&&(T(r,o)||j(r,o,t[e]?""+t[e]:s.join(String(e)))),t===y?t[e]=r:n?t[e]?t[e]=r:j(t,e,r):(delete t[e],j(t,e,r)))})(Function.prototype,e,function(){return"function"==typeof this&&this[o]||r.call(this)})}),L=function(n,i,t){if(function(t){if("function"!=typeof t)throw TypeError(t+" is not a function!")}(n),void 0===i)return n;switch(t){case 1:return function(t){return n.call(i,t)};case 2:return function(t,e){return n.call(i,t,e)};case 3:return function(t,e,r){return n.call(i,t,e,r)}}return function(){return n.apply(i,arguments)}},I="prototype",D=function(t,e,r){var n,i,o,s,a=t&D.F,u=t&D.G,c=t&D.S,f=t&D.P,h=t&D.B,l=u?y:c?y[e]||(y[e]={}):(y[e]||{})[I],p=u?v:v[e]||(v[e]={}),d=p[I]||(p[I]={});for(n in u&&(r=e),r)o=((i=!a&&l&&void 0!==l[n])?l:r)[n],s=h&&i?L(o,y):f&&"function"==typeof o?L(Function.call,o):o,l&&N(l,n,o,t&D.U),p[n]!=o&&j(p,n,s),f&&d[n]!=o&&(d[n]=o)};y.core=v,D.F=1,D.G=2,D.S=4,D.P=8,D.B=16,D.W=32,D.U=64,D.R=128;var R=D,C={}.toString,B=function(t){return C.call(t).slice(8,-1)},U=Object("z").propertyIsEnumerable(0)?Object:function(t){return"String"==B(t)?t.split(""):Object(t)},M=function(t){if(null==t)throw TypeError("Can't call method on  "+t);return t},z=function(t){return Object(M(t))},G=Math.ceil,W=Math.floor,H=function(t){return isNaN(t=+t)?0:(0<t?W:G)(t)},V=Math.min,q=function(t){return 0<t?V(H(t),9007199254740991):0},K=Array.isArray||function(t){return"Array"==B(t)},$="__core-js_shared__",J=y[$]||(y[$]={}),Y=function(t){return J[t]||(J[t]={})},X=e(function(t){var e=Y("wks"),r=y.Symbol,n="function"==typeof r;(t.exports=function(t){return e[t]||(e[t]=n&&r[t]||(n?r:F)("Symbol."+t))}).store=e}),Q=X("species"),Z=function(t,e){return K(r=t)&&("function"!=typeof(n=r.constructor)||n!==Array&&!K(n.prototype)||(n=void 0),l(n)&&null===(n=n[Q])&&(n=void 0)),new(void 0===n?Array:n)(e);var r,n},tt=function(h,t){var l=1==h,p=2==h,d=3==h,y=4==h,v=6==h,b=5==h||v,m=t||Z;return function(t,e,r){for(var n,i,o=z(t),s=U(o),a=L(e,r,3),u=q(s.length),c=0,f=l?m(t,u):p?m(t,0):void 0;c<u;c++)if((b||c in s)&&(i=a(n=s[c],c,o),h))if(l)f[c]=i;else if(i)switch(h){case 3:return!0;case 5:return n;case 6:return c;case 2:f.push(n)}else if(y)return!1;return v?-1:d||y?y:f}},et=X("unscopables"),rt=Array.prototype;null==rt[et]&&j(rt,et,{});var nt=function(t){rt[et][t]=!0},it=tt(5),ot="find",st=!0;ot in[]&&Array(1)[ot](function(){st=!1}),R(R.P+R.F*st,"Array",{find:function(t){return it(this,t,1<arguments.length?arguments[1]:void 0)}}),nt(ot);v.Array.find;var at=tt(6),ut="findIndex",ct=!0;ut in[]&&Array(1)[ut](function(){ct=!1}),R(R.P+R.F*ct,"Array",{findIndex:function(t){return at(this,t,1<arguments.length?arguments[1]:void 0)}}),nt(ut);v.Array.findIndex;var ft,ht=function(t){return U(M(t))},lt=Math.max,pt=Math.min,dt=Y("keys"),yt=function(t){return dt[t]||(dt[t]=F(t))},vt=(ft=!1,function(t,e,r){var n,i,o,s=ht(t),a=q(s.length),u=(i=a,(n=H(n=r))<0?lt(n+i,0):pt(n,i));if(ft&&e!=e){for(;u<a;)if((o=s[u++])!=o)return!0}else for(;u<a;u++)if((ft||u in s)&&s[u]===e)return ft||u||0;return!ft&&-1}),bt=yt("IE_PROTO"),mt=function(t,e){var r,n=ht(t),i=0,o=[];for(r in n)r!=bt&&T(n,r)&&o.push(r);for(;e.length>i;)T(n,r=e[i++])&&(~vt(o,r)||o.push(r));return o},_t="constructor,hasOwnProperty,isPrototypeOf,propertyIsEnumerable,toLocaleString,toString,valueOf".split(","),gt=Object.keys||function(t){return mt(t,_t)},wt={f:Object.getOwnPropertySymbols},Ot={f:{}.propertyIsEnumerable},St=Object.assign,Et=!St||d(function(){var t={},e={},r=Symbol(),n="abcdefghijklmnopqrst";return t[r]=7,n.split("").forEach(function(t){e[t]=t}),7!=St({},t)[r]||Object.keys(St({},e)).join("")!=n})?function(t,e){for(var r=z(t),n=arguments.length,i=1,o=wt.f,s=Ot.f;i<n;)for(var a,u=U(arguments[i++]),c=o?gt(u).concat(o(u)):gt(u),f=c.length,h=0;h<f;)s.call(u,a=c[h++])&&(r[a]=u[a]);return r}:St;R(R.S+R.F,"Object",{assign:Et});v.Object.assign;var At=X("match"),jt=function(t,e,r){if(l(n=e)&&(void 0!==(i=n[At])?i:"RegExp"==B(n)))throw TypeError("String#"+r+" doesn't accept regex!");var n,i;return String(M(t))},Pt=X("match"),Tt="startsWith",kt=""[Tt];R(R.P+R.F*function(e){var r=/./;try{"/./"[e](r)}catch(t){try{return r[Pt]=!1,!"/./"[e](r)}catch(t){}}return!0}(Tt),"String",{startsWith:function(t){var e=jt(this,t,Tt),r=q(Math.min(1<arguments.length?arguments[1]:void 0,e.length)),n=String(t);return kt?kt.call(e,n,r):e.slice(r,r+n.length)===n}});v.String.startsWith;R(R.P,"String",{repeat:function(t){var e=String(M(this)),r="",n=H(t);if(n<0||n==1/0)throw RangeError("Count can't be negative");for(;0<n;(n>>>=1)&&(e+=e))1&n&&(r+=e);return r}});v.String.repeat;var xt=e(function(t){var r=F("meta"),e=E.f,n=0,i=Object.isExtensible||function(){return!0},o=!d(function(){return i(Object.preventExtensions({}))}),s=function(t){e(t,r,{value:{i:"O"+ ++n,w:{}}})},a=t.exports={KEY:r,NEED:!1,fastKey:function(t,e){if(!l(t))return"symbol"==typeof t?t:("string"==typeof t?"S":"P")+t;if(!T(t,r)){if(!i(t))return"F";if(!e)return"E";s(t)}return t[r].i},getWeak:function(t,e){if(!T(t,r)){if(!i(t))return!0;if(!e)return!1;s(t)}return t[r].w},onFreeze:function(t){return o&&a.NEED&&i(t)&&!T(t,r)&&s(t),t}}}),Ft=(xt.KEY,xt.NEED,xt.fastKey,xt.getWeak,xt.onFreeze,E.f),Nt=X("toStringTag"),Lt=function(t,e,r){t&&!T(t=r?t:t.prototype,Nt)&&Ft(t,Nt,{configurable:!0,value:e})},It={f:X},Dt=E.f,Rt=function(t){var e=v.Symbol||(v.Symbol=y.Symbol||{});"_"==t.charAt(0)||t in e||Dt(e,t,{value:It.f(t)})},Ct=b?Object.defineProperties:function(t,e){p(t);for(var r,n=gt(e),i=n.length,o=0;o<i;)E.f(t,r=n[o++],e[r]);return t},Bt=y.document,Ut=Bt&&Bt.documentElement,Mt=yt("IE_PROTO"),zt=function(){},Gt="prototype",Wt=function(){var t,e=g("iframe"),r=_t.length;for(e.style.display="none",Ut.appendChild(e),e.src="javascript:",(t=e.contentWindow.document).open(),t.write("<script>document.F=Object<\/script>"),t.close(),Wt=t.F;r--;)delete Wt[Gt][_t[r]];return Wt()},Ht=Object.create||function(t,e){var r;return null!==t?(zt[Gt]=p(t),r=new zt,zt[Gt]=null,r[Mt]=t):r=Wt(),void 0===e?r:Ct(r,e)},Vt=_t.concat("length","prototype"),qt={f:Object.getOwnPropertyNames||function(t){return mt(t,Vt)}},Kt=qt.f,$t={}.toString,Jt="object"==typeof window&&window&&Object.getOwnPropertyNames?Object.getOwnPropertyNames(window):[],Yt={f:function(t){return Jt&&"[object Window]"==$t.call(t)?function(t){try{return Kt(t)}catch(t){return Jt.slice()}}(t):Kt(ht(t))}},Xt=Object.getOwnPropertyDescriptor,Qt={f:b?Xt:function(t,e){if(t=ht(t),e=O(e,!0),w)try{return Xt(t,e)}catch(t){}if(T(t,e))return A(!Ot.f.call(t,e),t[e])}},Zt=xt.KEY,te=Qt.f,ee=E.f,re=Yt.f,ne=y.Symbol,ie=y.JSON,oe=ie&&ie.stringify,se="prototype",ae=X("_hidden"),ue=X("toPrimitive"),ce={}.propertyIsEnumerable,fe=Y("symbol-registry"),he=Y("symbols"),le=Y("op-symbols"),pe=Object[se],de="function"==typeof ne,ye=y.QObject,ve=!ye||!ye[se]||!ye[se].findChild,be=b&&d(function(){return 7!=Ht(ee({},"a",{get:function(){return ee(this,"a",{value:7}).a}})).a})?function(t,e,r){var n=te(pe,e);n&&delete pe[e],ee(t,e,r),n&&t!==pe&&ee(pe,e,n)}:ee,me=function(t){var e=he[t]=Ht(ne[se]);return e._k=t,e},_e=de&&"symbol"==typeof ne.iterator?function(t){return"symbol"==typeof t}:function(t){return t instanceof ne},ge=function(t,e,r){return t===pe&&ge(le,e,r),p(t),e=O(e,!0),p(r),T(he,e)?(r.enumerable?(T(t,ae)&&t[ae][e]&&(t[ae][e]=!1),r=Ht(r,{enumerable:A(0,!1)})):(T(t,ae)||ee(t,ae,A(1,{})),t[ae][e]=!0),be(t,e,r)):ee(t,e,r)},we=function(t,e){p(t);for(var r,n=function(t){var e=gt(t),r=wt.f;if(r)for(var n,i=r(t),o=Ot.f,s=0;i.length>s;)o.call(t,n=i[s++])&&e.push(n);return e}(e=ht(e)),i=0,o=n.length;i<o;)ge(t,r=n[i++],e[r]);return t},Oe=function(t){var e=ce.call(this,t=O(t,!0));return!(this===pe&&T(he,t)&&!T(le,t))&&(!(e||!T(this,t)||!T(he,t)||T(this,ae)&&this[ae][t])||e)},Se=function(t,e){if(t=ht(t),e=O(e,!0),t!==pe||!T(he,e)||T(le,e)){var r=te(t,e);return!r||!T(he,e)||T(t,ae)&&t[ae][e]||(r.enumerable=!0),r}},Ee=function(t){for(var e,r=re(ht(t)),n=[],i=0;r.length>i;)T(he,e=r[i++])||e==ae||e==Zt||n.push(e);return n},Ae=function(t){for(var e,r=t===pe,n=re(r?le:ht(t)),i=[],o=0;n.length>o;)!T(he,e=n[o++])||r&&!T(pe,e)||i.push(he[e]);return i};de||(N((ne=function(){if(this instanceof ne)throw TypeError("Symbol is not a constructor!");var e=F(0<arguments.length?arguments[0]:void 0),r=function(t){this===pe&&r.call(le,t),T(this,ae)&&T(this[ae],e)&&(this[ae][e]=!1),be(this,e,A(1,t))};return b&&ve&&be(pe,e,{configurable:!0,set:r}),me(e)})[se],"toString",function(){return this._k}),Qt.f=Se,E.f=ge,qt.f=Yt.f=Ee,Ot.f=Oe,wt.f=Ae,b&&N(pe,"propertyIsEnumerable",Oe,!0),It.f=function(t){return me(X(t))}),R(R.G+R.W+R.F*!de,{Symbol:ne});for(var je="hasInstance,isConcatSpreadable,iterator,match,replace,search,species,split,toPrimitive,toStringTag,unscopables".split(","),Pe=0;je.length>Pe;)X(je[Pe++]);for(var Te=gt(X.store),ke=0;Te.length>ke;)Rt(Te[ke++]);R(R.S+R.F*!de,"Symbol",{for:function(t){return T(fe,t+="")?fe[t]:fe[t]=ne(t)},keyFor:function(t){if(!_e(t))throw TypeError(t+" is not a symbol!");for(var e in fe)if(fe[e]===t)return e},useSetter:function(){ve=!0},useSimple:function(){ve=!1}}),R(R.S+R.F*!de,"Object",{create:function(t,e){return void 0===e?Ht(t):we(Ht(t),e)},defineProperty:ge,defineProperties:we,getOwnPropertyDescriptor:Se,getOwnPropertyNames:Ee,getOwnPropertySymbols:Ae}),ie&&R(R.S+R.F*(!de||d(function(){var t=ne();return"[null]"!=oe([t])||"{}"!=oe({a:t})||"{}"!=oe(Object(t))})),"JSON",{stringify:function(t){for(var e,r,n=[t],i=1;arguments.length>i;)n.push(arguments[i++]);if(r=e=n[1],(l(e)||void 0!==t)&&!_e(t))return K(e)||(e=function(t,e){if("function"==typeof r&&(e=r.call(this,t,e)),!_e(e))return e}),n[1]=e,oe.apply(ie,n)}}),ne[se][ue]||j(ne[se],ue,ne[se].valueOf),Lt(ne,"Symbol"),Lt(Math,"Math",!0),Lt(y.JSON,"JSON",!0);var xe=X("toStringTag"),Fe="Arguments"==B(function(){return arguments}()),Ne={};Ne[X("toStringTag")]="z",Ne+""!="[object z]"&&N(Object.prototype,"toString",function(){return"[object "+(void 0===(t=this)?"Undefined":null===t?"Null":"string"==typeof(r=function(t,e){try{return t[e]}catch(t){}}(e=Object(t),xe))?r:Fe?B(e):"Object"==(n=B(e))&&"function"==typeof e.callee?"Arguments":n)+"]";var t,e,r,n},!0),Rt("asyncIterator"),Rt("observable");v.Symbol;var Le={},Ie={};j(Ie,X("iterator"),function(){return this});var De,Re=yt("IE_PROTO"),Ce=Object.prototype,Be=Object.getPrototypeOf||function(t){return t=z(t),T(t,Re)?t[Re]:"function"==typeof t.constructor&&t instanceof t.constructor?t.constructor.prototype:t instanceof Object?Ce:null},Ue=X("iterator"),Me=!([].keys&&"next"in[].keys()),ze="values",Ge=function(){return this},We=function(t,e,r,n,i,o,s){var a,u,c;u=e,c=n,(a=r).prototype=Ht(Ie,{next:A(1,c)}),Lt(a,u+" Iterator");var f,h,l,p=function(t){if(!Me&&t in b)return b[t];switch(t){case"keys":case ze:return function(){return new r(this,t)}}return function(){return new r(this,t)}},d=e+" Iterator",y=i==ze,v=!1,b=t.prototype,m=b[Ue]||b["@@iterator"]||i&&b[i],_=m||p(i),g=i?y?p("entries"):_:void 0,w="Array"==e&&b.entries||m;if(w&&(l=Be(w.call(new t)))!==Object.prototype&&l.next&&(Lt(l,d,!0),"function"!=typeof l[Ue]&&j(l,Ue,Ge)),y&&m&&m.name!==ze&&(v=!0,_=function(){return m.call(this)}),(Me||v||!b[Ue])&&j(b,Ue,_),Le[e]=_,Le[d]=Ge,i)if(f={values:y?_:p(ze),keys:o?_:p("keys"),entries:g},s)for(h in f)h in b||N(b,h,f[h]);else R(R.P+R.F*(Me||v),e,f);return f},He=(De=!0,function(t,e){var r,n,i=String(M(t)),o=H(e),s=i.length;return o<0||s<=o?De?"":void 0:(r=i.charCodeAt(o))<55296||56319<r||o+1===s||(n=i.charCodeAt(o+1))<56320||57343<n?De?i.charAt(o):r:De?i.slice(o,o+2):n-56320+(r-55296<<10)+65536});We(String,"String",function(t){this._t=String(t),this._i=0},function(){var t,e=this._t,r=this._i;return r>=e.length?{value:void 0,done:!0}:(t=He(e,r),this._i+=t.length,{value:t,done:!1})});var Ve=function(t,e){return{value:e,done:!!t}},qe=We(Array,"Array",function(t,e){this._t=ht(t),this._i=0,this._k=e},function(){var t=this._t,e=this._k,r=this._i++;return!t||r>=t.length?(this._t=void 0,Ve(1)):Ve(0,"keys"==e?r:"values"==e?t[r]:[r,t[r]])},"values");Le.Arguments=Le.Array,nt("keys"),nt("values"),nt("entries");for(var Ke=X("iterator"),$e=X("toStringTag"),Je=Le.Array,Ye={CSSRuleList:!0,CSSStyleDeclaration:!1,CSSValueList:!1,ClientRectList:!1,DOMRectList:!1,DOMStringList:!1,DOMTokenList:!0,DataTransferItemList:!1,FileList:!1,HTMLAllCollection:!1,HTMLCollection:!1,HTMLFormElement:!1,HTMLSelectElement:!1,MediaList:!0,MimeTypeArray:!1,NamedNodeMap:!1,NodeList:!0,PaintRequestList:!1,Plugin:!1,PluginArray:!1,SVGLengthList:!1,SVGNumberList:!1,SVGPathSegList:!1,SVGPointList:!1,SVGStringList:!1,SVGTransformList:!1,SourceBufferList:!1,StyleSheetList:!0,TextTrackCueList:!1,TextTrackList:!1,TouchList:!1},Xe=gt(Ye),Qe=0;Qe<Xe.length;Qe++){var Ze,tr=Xe[Qe],er=Ye[tr],rr=y[tr],nr=rr&&rr.prototype;if(nr&&(nr[Ke]||j(nr,Ke,Je),nr[$e]||j(nr,$e,tr),Le[tr]=Je,er))for(Ze in qe)nr[Ze]||N(nr,Ze,qe[Ze],!0)}It.f("iterator");var ir=Object.setPrototypeOf||{__proto__:[]}instanceof Array&&function(t,e){t.__proto__=e}||function(t,e){for(var r in e)e.hasOwnProperty(r)&&(t[r]=e[r])};function or(t,e){if(!(e instanceof Object))return e;switch(e.constructor){case Date:return new Date(e.getTime());case Object:void 0===t&&(t={});break;case Array:t=[];break;default:return e}for(var r in e)e.hasOwnProperty(r)&&(t[r]=or(t[r],e[r]));return t}function sr(t,e,r){t[e]=r}var ar="FirebaseError",ur=Error.captureStackTrace,cr=function(t,e){if(this.code=t,this.message=e,ur)ur(this,fr.prototype.create);else try{throw Error.apply(this,arguments)}catch(t){this.name=ar,Object.defineProperty(this,"stack",{get:function(){return t.stack}})}};cr.prototype=Object.create(Error.prototype),(cr.prototype.constructor=cr).prototype.name=ar;var fr=function(){function t(t,e,r){this.service=t,this.serviceName=e,this.errors=r,this.pattern=/\{\$([^}]+)}/g}return t.prototype.create=function(t,n){void 0===n&&(n={});var e,r=this.errors[t],i=this.service+"/"+t;e=void 0===r?"Error":r.replace(this.pattern,function(t,e){var r=n[e];return void 0!==r?r.toString():"<"+e+"?>"}),e=this.serviceName+": "+e+" ("+i+").";var o=new cr(i,e);for(var s in n)n.hasOwnProperty(s)&&"_"!==s.slice(-1)&&(o[s]=n[s]);return o},t}();!function(r){function t(){var t=r.call(this)||this;t.chain_=[],t.buf_=[],t.W_=[],t.pad_=[],t.inbuf_=0,t.total_=0,t.blockSize=64,t.pad_[0]=128;for(var e=1;e<t.blockSize;++e)t.pad_[e]=0;return t.reset(),t}(function(t,e){function r(){this.constructor=t}ir(t,e),t.prototype=null===e?Object.create(e):(r.prototype=e.prototype,new r)})(t,r),t.prototype.reset=function(){this.chain_[0]=1732584193,this.chain_[1]=4023233417,this.chain_[2]=2562383102,this.chain_[3]=271733878,this.chain_[4]=3285377520,this.inbuf_=0,this.total_=0},t.prototype.compress_=function(t,e){e||(e=0);var r=this.W_;if("string"==typeof t)for(var n=0;n<16;n++)r[n]=t.charCodeAt(e)<<24|t.charCodeAt(e+1)<<16|t.charCodeAt(e+2)<<8|t.charCodeAt(e+3),e+=4;else for(n=0;n<16;n++)r[n]=t[e]<<24|t[e+1]<<16|t[e+2]<<8|t[e+3],e+=4;for(n=16;n<80;n++){var i=r[n-3]^r[n-8]^r[n-14]^r[n-16];r[n]=4294967295&(i<<1|i>>>31)}var o,s,a=this.chain_[0],u=this.chain_[1],c=this.chain_[2],f=this.chain_[3],h=this.chain_[4];for(n=0;n<80;n++){s=n<40?n<20?(o=f^u&(c^f),1518500249):(o=u^c^f,1859775393):n<60?(o=u&c|f&(u|c),2400959708):(o=u^c^f,3395469782);i=(a<<5|a>>>27)+o+h+s+r[n]&4294967295;h=f,f=c,c=4294967295&(u<<30|u>>>2),u=a,a=i}this.chain_[0]=this.chain_[0]+a&4294967295,this.chain_[1]=this.chain_[1]+u&4294967295,this.chain_[2]=this.chain_[2]+c&4294967295,this.chain_[3]=this.chain_[3]+f&4294967295,this.chain_[4]=this.chain_[4]+h&4294967295},t.prototype.update=function(t,e){if(null!=t){void 0===e&&(e=t.length);for(var r=e-this.blockSize,n=0,i=this.buf_,o=this.inbuf_;n<e;){if(0==o)for(;n<=r;)this.compress_(t,n),n+=this.blockSize;if("string"==typeof t){for(;n<e;)if(i[o]=t.charCodeAt(n),++n,++o==this.blockSize){this.compress_(i),o=0;break}}else for(;n<e;)if(i[o]=t[n],++n,++o==this.blockSize){this.compress_(i),o=0;break}}this.inbuf_=o,this.total_+=e}},t.prototype.digest=function(){var t=[],e=8*this.total_;this.inbuf_<56?this.update(this.pad_,56-this.inbuf_):this.update(this.pad_,this.blockSize-(this.inbuf_-56));for(var r=this.blockSize-1;56<=r;r--)this.buf_[r]=255&e,e/=256;this.compress_(this.buf_);var n=0;for(r=0;r<5;r++)for(var i=24;0<=i;i-=8)t[n]=this.chain_[r]>>i&255,++n;return t}}(function(){this.blockSize=-1});function hr(t,e){var r=new lr(t,e);return r.subscribe.bind(r)}var lr=function(){function t(t,e){var r=this;this.observers=[],this.unsubscribes=[],this.observerCount=0,this.task=Promise.resolve(),this.finalized=!1,this.onNoObservers=e,this.task.then(function(){t(r)}).catch(function(t){r.error(t)})}return t.prototype.next=function(e){this.forEachObserver(function(t){t.next(e)})},t.prototype.error=function(e){this.forEachObserver(function(t){t.error(e)}),this.close(e)},t.prototype.complete=function(){this.forEachObserver(function(t){t.complete()}),this.close()},t.prototype.subscribe=function(t,e,r){var n,i=this;if(void 0===t&&void 0===e&&void 0===r)throw new Error("Missing Observer.");void 0===(n=function(t,e){if("object"!=typeof t||null===t)return!1;for(var r=0,n=e;r<n.length;r++){var i=n[r];if(i in t&&"function"==typeof t[i])return!0}return!1}(t,["next","error","complete"])?t:{next:t,error:e,complete:r}).next&&(n.next=pr),void 0===n.error&&(n.error=pr),void 0===n.complete&&(n.complete=pr);var o=this.unsubscribeOne.bind(this,this.observers.length);return this.finalized&&this.task.then(function(){try{i.finalError?n.error(i.finalError):n.complete()}catch(t){}}),this.observers.push(n),o},t.prototype.unsubscribeOne=function(t){void 0!==this.observers&&void 0!==this.observers[t]&&(delete this.observers[t],this.observerCount-=1,0===this.observerCount&&void 0!==this.onNoObservers&&this.onNoObservers(this))},t.prototype.forEachObserver=function(t){if(!this.finalized)for(var e=0;e<this.observers.length;e++)this.sendOne(e,t)},t.prototype.sendOne=function(t,e){var r=this;this.task.then(function(){if(void 0!==r.observers&&void 0!==r.observers[t])try{e(r.observers[t])}catch(t){"undefined"!=typeof console&&console.error&&console.error(t)}})},t.prototype.close=function(t){var e=this;this.finalized||(this.finalized=!0,void 0!==t&&(this.finalError=t),this.task.then(function(){e.observers=void 0,e.onNoObservers=void 0}))},t}();function pr(){}var dr=function(t,e){return Object.prototype.hasOwnProperty.call(t,e)},yr="[DEFAULT]",vr=[],br=function(){function t(t,e,r){this.firebase_=r,this.isDeleted_=!1,this.services_={},this.name_=e.name,this._automaticDataCollectionEnabled=e.automaticDataCollectionEnabled||!1,this.options_=or(void 0,t),this.INTERNAL={getUid:function(){return null},getToken:function(){return Promise.resolve(null)},addAuthTokenListener:function(t){vr.push(t),setTimeout(function(){return t(null)},0)},removeAuthTokenListener:function(e){vr=vr.filter(function(t){return t!==e})}}}return Object.defineProperty(t.prototype,"automaticDataCollectionEnabled",{get:function(){return this.checkDestroyed_(),this._automaticDataCollectionEnabled},set:function(t){this.checkDestroyed_(),this._automaticDataCollectionEnabled=t},enumerable:!0,configurable:!0}),Object.defineProperty(t.prototype,"name",{get:function(){return this.checkDestroyed_(),this.name_},enumerable:!0,configurable:!0}),Object.defineProperty(t.prototype,"options",{get:function(){return this.checkDestroyed_(),this.options_},enumerable:!0,configurable:!0}),t.prototype.delete=function(){var n=this;return new Promise(function(t){n.checkDestroyed_(),t()}).then(function(){n.firebase_.INTERNAL.removeApp(n.name_);var r=[];return Object.keys(n.services_).forEach(function(e){Object.keys(n.services_[e]).forEach(function(t){r.push(n.services_[e][t])})}),Promise.all(r.map(function(t){return t.INTERNAL.delete()}))}).then(function(){n.isDeleted_=!0,n.services_={}})},t.prototype._getService=function(t,e){if(void 0===e&&(e=yr),this.checkDestroyed_(),this.services_[t]||(this.services_[t]={}),!this.services_[t][e]){var r=e!==yr?e:void 0,n=this.firebase_.INTERNAL.factories[t](this,this.extendApp.bind(this),r);this.services_[t][e]=n}return this.services_[t][e]},t.prototype.extendApp=function(t){var e=this;or(this,t),t.INTERNAL&&t.INTERNAL.addAuthTokenListener&&(vr.forEach(function(t){e.INTERNAL.addAuthTokenListener(t)}),vr=[])},t.prototype.checkDestroyed_=function(){this.isDeleted_&&mr("app-deleted",{name:this.name_})},t}();function mr(t,e){throw _r.create(t,e)}br.prototype.name&&br.prototype.options||br.prototype.delete||console.log("dc");var _r=new fr("app","Firebase",{"no-app":"No Firebase App '{$name}' has been created - call Firebase App.initializeApp()","bad-app-name":"Illegal App name: '{$name}","duplicate-app":"Firebase App named '{$name}' already exists","app-deleted":"Firebase App named '{$name}' already deleted","duplicate-service":"Firebase service named '{$name}' already registered","sa-not-supported":"Initializing the Firebase SDK with a service account is only allowed in a Node.js environment. On client devices, you should instead initialize the SDK with an api key and auth domain","invalid-app-argument":"firebase.{$name}() takes either no argument or a Firebase App instance."}),gr=!1;try{gr="[object process]"===Object.prototype.toString.call(global.process)}catch(t){}return gr&&console.warn('\nWarning: This is a browser-targeted Firebase bundle but it appears it is being\nrun in a Node environment.  If running in a Node environment, make sure you\nare using the bundle specified by the "main" field in package.json.\n\nIf you are using Webpack, you can specify "main" as the first item in\n"resolve.mainFields":\nhttps://webpack.js.org/configuration/resolve/#resolvemainfields\n\nIf using Rollup, use the rollup-plugin-node-resolve plugin and set "module"\nto false and "main" to true:\nhttps://github.com/rollup/rollup-plugin-node-resolve\n'),function t(){var s={},a={},u={},c={__esModule:!0,initializeApp:function(t,e){if(void 0===e&&(e={}),"object"!=typeof e||null===e){var r=e;e={name:r}}var n=e;void 0===n.name&&(n.name=yr);var i=n.name;"string"==typeof i&&i||mr("bad-app-name",{name:i+""}),dr(s,i)&&mr("duplicate-app",{name:i});var o=new br(t,n,c);return l(s[i]=o,"create"),o},app:f,apps:null,Promise:Promise,SDK_VERSION:"5.8.5",INTERNAL:{registerService:function(r,t,e,n,i){a[r]&&mr("duplicate-service",{name:r}),a[r]=t,n&&(u[r]=n,h().forEach(function(t){n("create",t)}));var o=function(t){return void 0===t&&(t=f()),"function"!=typeof t[r]&&mr("invalid-app-argument",{name:r}),t[r]()};return void 0!==e&&or(o,e),c[r]=o,br.prototype[r]=function(){for(var t=[],e=0;e<arguments.length;e++)t[e]=arguments[e];return this._getService.bind(this,r).apply(this,i?t:[])},o},createFirebaseNamespace:t,extendNamespace:function(t){or(c,t)},createSubscribe:hr,ErrorFactory:fr,removeApp:function(t){l(s[t],"delete"),delete s[t]},factories:a,useAsService:i,Promise:Promise,deepExtend:or}};function f(t){return dr(s,t=t||yr)||mr("no-app",{name:t}),s[t]}function h(){return Object.keys(s).map(function(t){return s[t]})}function l(r,n){Object.keys(a).forEach(function(t){var e=i(r,t);null!==e&&u[e]&&u[e](n,r)})}function i(t,e){if("serverAuth"===e)return null;var r=e;return t.options,r}return sr(c,"default",c),Object.defineProperty(c,"apps",{get:h}),sr(f,"App",br),c}()});
//# sourceMappingURL=firebase-app.js.map
