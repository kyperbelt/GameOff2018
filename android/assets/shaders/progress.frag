#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;
varying vec2 v_texCoords;
uniform sampler2D u_texture;
uniform float u_progress;
uniform float u_height;

void main() {

	vec4 texColor = texture2D(u_texture, v_texCoords);


	
	float p = (gl_Position.y + v_texCoords.y) / u_height;
	
	if(p > u_progress){
		texColor.a = 0f;
	}
	

	
	gl_FragColor = v_color * texColor;
}
