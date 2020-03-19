uniform vec4 UNIFORMS;

varying vec2 dots;

void main(void) {
	// Offset window-space fragment position.
	vec3 color2D = vec3(gl_FragCoord + UNIFORMS.zwxx);

	// Normalize position.
	// Multiply with current sin.
	color2D.xy = normalize(color2D).xy * UNIFORMS.x;
	// {-1..1} => {0..1} & Intensify colors.
	color2D.xy = (vec2(color2D) * 0.5 + 0.5) * 2.0;
	color2D.z = 1.0;
	
	// Accumulate color contributions.
	// Hardcoded ambience and specular color, due to buggy drivers.
	color2D = dots.x * color2D + vec3(0.2, 0.2, 0.2);
	gl_FragColor.rgb = pow(dots.y, UNIFORMS.y) * vec3(1.0, 1.0, 0.5) + color2D;
	gl_FragColor.a = 1.0;
}