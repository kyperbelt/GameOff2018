#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform float u_progress;

void main() {

	vec4 texColor = texture2D(u_texture, v_texCoords);


	vec2 position = gl_FragCoord.xy;


	if(v_texCoords.y < progress){
		texColor.a = 0f;
	}

	gl_FragColor = v_color * texColor;
}
