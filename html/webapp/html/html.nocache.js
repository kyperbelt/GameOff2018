function html(){var O='bootstrap',P='begin',Q='gwt.codesvr.html=',R='gwt.codesvr=',S='html',T='startup',U='DUMMY',V=0,W=1,X='iframe',Y='javascript:""',Z='position:absolute; width:0; height:0; border:none; left: -1000px;',$=' top: -1000px;',_='CSS1Compat',ab='<!doctype html>',bb='',cb='<html><head><\/head><body><\/body><\/html>',db='undefined',eb='readystatechange',fb=10,gb='Chrome',hb='eval("',ib='");',jb='script',kb='javascript',lb='moduleStartup',mb='moduleRequested',nb='Failed to load ',ob='head',pb='meta',qb='name',rb='html::',sb='::',tb='gwt:property',ub='content',vb='=',wb='gwt:onPropertyErrorFn',xb='Bad handler "',yb='" for "gwt:onPropertyErrorFn"',zb='gwt:onLoadErrorFn',Ab='" for "gwt:onLoadErrorFn"',Bb='#',Cb='?',Db='/',Eb='img',Fb='clear.cache.gif',Gb='baseUrl',Hb='html.nocache.js',Ib='base',Jb='//',Kb='user.agent',Lb='webkit',Mb='safari',Nb='msie',Ob=11,Pb='ie10',Qb=9,Rb='ie9',Sb=8,Tb='ie8',Ub='gecko',Vb='gecko1_8',Wb=2,Xb=3,Yb=4,Zb='selectingPermutation',$b='html.devmode.js',_b='3EB9464E5E13F7D9C7805E82CC5DF22A',ac='4F4852961416E277BA669FC83CD42CED',bc='A563A938600EADCF67696ABD46E3A8E0',cc='C64FFAB9B5469792676EA7F66ACAE7E2',dc='EAF1B8810E334BE99975AC6B15F10D7E',ec=':',fc='.cache.js',gc='link',hc='rel',ic='stylesheet',jc='href',kc='loadExternalRefs',lc='gwt/chrome/chrome.css',mc='end',nc='http:',oc='file:',pc='_gwt_dummy_',qc='__gwtDevModeHook:html',rc='Ignoring non-whitelisted Dev Mode URL: ',sc=':moduleBase';var o=window;var p=document;r(O,P);function q(){var a=o.location.search;return a.indexOf(Q)!=-1||a.indexOf(R)!=-1}
function r(a,b){if(o.__gwtStatsEvent){o.__gwtStatsEvent({moduleName:S,sessionId:o.__gwtStatsSessionId,subSystem:T,evtGroup:a,millis:(new Date).getTime(),type:b})}}
html.__sendStats=r;html.__moduleName=S;html.__errFn=null;html.__moduleBase=U;html.__softPermutationId=V;html.__computePropValue=null;html.__getPropMap=null;html.__installRunAsyncCode=function(){};html.__gwtStartLoadingFragment=function(){return null};html.__gwt_isKnownPropertyValue=function(){return false};html.__gwt_getMetaProperty=function(){return null};var s=null;var t=o.__gwt_activeModules=o.__gwt_activeModules||{};t[S]={moduleName:S};html.__moduleStartupDone=function(e){var f=t[S].bindings;t[S].bindings=function(){var a=f?f():{};var b=e[html.__softPermutationId];for(var c=V;c<b.length;c++){var d=b[c];a[d[V]]=d[W]}return a}};var u;function v(){w();return u}
function w(){if(u){return}var a=p.createElement(X);a.src=Y;a.id=S;a.style.cssText=Z+$;a.tabIndex=-1;p.body.appendChild(a);u=a.contentDocument;if(!u){u=a.contentWindow.document}u.open();var b=document.compatMode==_?ab:bb;u.write(b+cb);u.close()}
function A(k){function l(a){function b(){if(typeof p.readyState==db){return typeof p.body!=db&&p.body!=null}return /loaded|complete/.test(p.readyState)}
var c=b();if(c){a();return}function d(){if(!c){if(!b()){return}c=true;a();if(p.removeEventListener){p.removeEventListener(eb,d,false)}if(e){clearInterval(e)}}}
if(p.addEventListener){p.addEventListener(eb,d,false)}var e=setInterval(function(){d()},fb)}
function m(c){function d(a,b){a.removeChild(b)}
var e=v();var f=e.body;var g;if(navigator.userAgent.indexOf(gb)>-1&&window.JSON){var h=e.createDocumentFragment();h.appendChild(e.createTextNode(hb));for(var i=V;i<c.length;i++){var j=window.JSON.stringify(c[i]);h.appendChild(e.createTextNode(j.substring(W,j.length-W)))}h.appendChild(e.createTextNode(ib));g=e.createElement(jb);g.language=kb;g.appendChild(h);f.appendChild(g);d(f,g)}else{for(var i=V;i<c.length;i++){g=e.createElement(jb);g.language=kb;g.text=c[i];f.appendChild(g);d(f,g)}}}
html.onScriptDownloaded=function(a){l(function(){m(a)})};r(lb,mb);var n=p.createElement(jb);n.src=k;if(html.__errFn){n.onerror=function(){html.__errFn(S,new Error(nb+code))}}p.getElementsByTagName(ob)[V].appendChild(n)}
html.__startLoadingFragment=function(a){return D(a)};html.__installRunAsyncCode=function(a){var b=v();var c=b.body;var d=b.createElement(jb);d.language=kb;d.text=a;c.appendChild(d);c.removeChild(d)};function B(){var c={};var d;var e;var f=p.getElementsByTagName(pb);for(var g=V,h=f.length;g<h;++g){var i=f[g],j=i.getAttribute(qb),k;if(j){j=j.replace(rb,bb);if(j.indexOf(sb)>=V){continue}if(j==tb){k=i.getAttribute(ub);if(k){var l,m=k.indexOf(vb);if(m>=V){j=k.substring(V,m);l=k.substring(m+W)}else{j=k;l=bb}c[j]=l}}else if(j==wb){k=i.getAttribute(ub);if(k){try{d=eval(k)}catch(a){alert(xb+k+yb)}}}else if(j==zb){k=i.getAttribute(ub);if(k){try{e=eval(k)}catch(a){alert(xb+k+Ab)}}}}}__gwt_getMetaProperty=function(a){var b=c[a];return b==null?null:b};s=d;html.__errFn=e}
function C(){function e(a){var b=a.lastIndexOf(Bb);if(b==-1){b=a.length}var c=a.indexOf(Cb);if(c==-1){c=a.length}var d=a.lastIndexOf(Db,Math.min(c,b));return d>=V?a.substring(V,d+W):bb}
function f(a){if(a.match(/^\w+:\/\//)){}else{var b=p.createElement(Eb);b.src=a+Fb;a=e(b.src)}return a}
function g(){var a=__gwt_getMetaProperty(Gb);if(a!=null){return a}return bb}
function h(){var a=p.getElementsByTagName(jb);for(var b=V;b<a.length;++b){if(a[b].src.indexOf(Hb)!=-1){return e(a[b].src)}}return bb}
function i(){var a=p.getElementsByTagName(Ib);if(a.length>V){return a[a.length-W].href}return bb}
function j(){var a=p.location;return a.href==a.protocol+Jb+a.host+a.pathname+a.search+a.hash}
var k=g();if(k==bb){k=h()}if(k==bb){k=i()}if(k==bb&&j()){k=e(p.location.href)}k=f(k);return k}
function D(a){if(a.match(/^\//)){return a}if(a.match(/^[a-zA-Z]+:\/\//)){return a}return html.__moduleBase+a}
function F(){var f=[];var g=V;function h(a,b){var c=f;for(var d=V,e=a.length-W;d<e;++d){c=c[a[d]]||(c[a[d]]=[])}c[a[e]]=b}
var i=[];var j=[];function k(a){var b=j[a](),c=i[a];if(b in c){return b}var d=[];for(var e in c){d[c[e]]=e}if(s){s(a,d,b)}throw null}
j[Kb]=function(){var a=navigator.userAgent.toLowerCase();var b=p.documentMode;if(function(){return a.indexOf(Lb)!=-1}())return Mb;if(function(){return a.indexOf(Nb)!=-1&&(b>=fb&&b<Ob)}())return Pb;if(function(){return a.indexOf(Nb)!=-1&&(b>=Qb&&b<Ob)}())return Rb;if(function(){return a.indexOf(Nb)!=-1&&(b>=Sb&&b<Ob)}())return Tb;if(function(){return a.indexOf(Ub)!=-1||b>=Ob}())return Vb;return bb};i[Kb]={'gecko1_8':V,'ie10':W,'ie8':Wb,'ie9':Xb,'safari':Yb};__gwt_isKnownPropertyValue=function(a,b){return b in i[a]};html.__getPropMap=function(){var a={};for(var b in i){if(i.hasOwnProperty(b)){a[b]=k(b)}}return a};html.__computePropValue=k;o.__gwt_activeModules[S].bindings=html.__getPropMap;r(O,Zb);if(q()){return D($b)}var l;try{h([Pb],_b);h([Mb],ac);h([Vb],bc);h([Rb],cc);h([Tb],dc);l=f[k(Kb)];var m=l.indexOf(ec);if(m!=-1){g=parseInt(l.substring(m+W),fb);l=l.substring(V,m)}}catch(a){}html.__softPermutationId=g;return D(l+fc)}
function G(){if(!o.__gwt_stylesLoaded){o.__gwt_stylesLoaded={}}function c(a){if(!__gwt_stylesLoaded[a]){var b=p.createElement(gc);b.setAttribute(hc,ic);b.setAttribute(jc,D(a));p.getElementsByTagName(ob)[V].appendChild(b);__gwt_stylesLoaded[a]=true}}
r(kc,P);c(lc);r(kc,mc)}
B();html.__moduleBase=C();t[S].moduleBase=html.__moduleBase;var H=F();if(o){var I=!!(o.location.protocol==nc||o.location.protocol==oc);o.__gwt_activeModules[S].canRedirect=I;function J(){var b=pc;try{o.sessionStorage.setItem(b,b);o.sessionStorage.removeItem(b);return true}catch(a){return false}}
if(I&&J()){var K=qc;var L=o.sessionStorage[K];if(!/^http:\/\/(localhost|127\.0\.0\.1)(:\d+)?\/.*$/.test(L)){if(L&&(window.console&&console.log)){console.log(rc+L)}L=bb}if(L&&!o[K]){o[K]=true;o[K+sc]=C();var M=p.createElement(jb);M.src=L;var N=p.getElementsByTagName(ob)[V];N.insertBefore(M,N.firstElementChild||N.children[V]);return false}}}G();r(O,mc);A(H);return true}
html.succeeded=html();